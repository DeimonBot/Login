package com.prueba.Prueba.Usuarios.service;

import com.prueba.Prueba.Usuarios.dtos.CargaMasivaResponseDTO;
import com.prueba.Prueba.Usuarios.dtos.CuentaCreadaDTO;
import com.prueba.Prueba.Usuarios.dtos.ErrorFilaDTO;
import com.prueba.Prueba.Usuarios.dtos.EstudianteCsv;
import com.prueba.Prueba.Usuarios.entitys.Rol;
import com.prueba.Prueba.Usuarios.entitys.Usuario;
import com.prueba.Prueba.Usuarios.exception.CsvArchivoInvalidoException;
import com.prueba.Prueba.Usuarios.exception.CsvFilaInvalidaException;
import com.prueba.Prueba.Usuarios.repository.UsuarioRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CsvParserService csvParserService;
    private final Validator validator;

    @Transactional
    public CargaMasivaResponseDTO cargarEstudiantesDesdeCsv(MultipartFile archivo) {
        validarArchivo(archivo);

        List<EstudianteCsv> estudiantes = csvParserService.parsearArchivo(archivo);
        return procesarEstudiantes(estudiantes);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new CsvArchivoInvalidoException("El archivo está vacío");
        }

        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.toLowerCase().endsWith(".csv")) {
            throw new CsvArchivoInvalidoException("El archivo debe tener extensión .csv");
        }
    }

    private CargaMasivaResponseDTO procesarEstudiantes(List<EstudianteCsv> estudiantes) {
        List<ErrorFilaDTO> errores = new ArrayList<>();
        List<CuentaCreadaDTO> cuentasCreadas = new ArrayList<>();
        int creados = 0;
        int actualizados = 0;

        for (int i = 0; i < estudiantes.size(); i++) {
            EstudianteCsv dto = estudiantes.get(i);
            int numeroFila = i + 2; // +2 porque la fila 1 es el encabezado

            try {
                validarEstudiante(dto, numeroFila);

                ResultadoOperacion resultado = guardarOActualizarEstudiante(dto);

                if (resultado.esNuevo()) {
                    creados++;
                    Usuario u = resultado.usuario();
                    cuentasCreadas.add(new CuentaCreadaDTO(u.getCedula(), u.getCorreo(), u.getPassword()));
                } else {
                    actualizados++;
                }

            } catch (CsvFilaInvalidaException e) {
                errores.add(new ErrorFilaDTO(numeroFila, "validacion", e.getMessage()));
            } catch (Exception e) {
                log.error("Error inesperado en fila {}", numeroFila, e);
                errores.add(new ErrorFilaDTO(numeroFila, "sistema", "Error inesperado: " + e.getMessage()));
            }
        }

        return construirRespuesta(creados, actualizados, errores, cuentasCreadas);
    }

    private void validarEstudiante(EstudianteCsv dto, int numeroFila) {
        Set<ConstraintViolation<EstudianteCsv>> violaciones = validator.validate(dto);

        if (!violaciones.isEmpty()) {
            String mensajes = violaciones.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new CsvFilaInvalidaException(numeroFila, mensajes);
        }
    }

    private ResultadoOperacion guardarOActualizarEstudiante(EstudianteCsv dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCedula(dto.cc());
        boolean esNuevo = usuarioExistente.isEmpty();
        Usuario usuario = esNuevo ? crearUsuario(dto) : actualizarUsuario(usuarioExistente.get(), dto);
        usuarioRepository.save(usuario);
        return new ResultadoOperacion(esNuevo, usuario);
    }

    private Usuario crearUsuario(EstudianteCsv dto) {
        String password = dto.nombre().toUpperCase().charAt(0) + dto.cc() + "*";
        return Usuario.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .cedula(dto.cc())
                .correo(dto.correo())
                .telefono(dto.telefono())
                .carrera(dto.carrera())
                .rol(Rol.ESTUDIANTE)
                .password(password)
                .build();
    }

    private Usuario actualizarUsuario(Usuario usuario, EstudianteCsv dto) {
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setCorreo(dto.correo());
        usuario.setTelefono(dto.telefono());
        usuario.setCarrera(dto.carrera());
        return usuario;
    }

    private CargaMasivaResponseDTO construirRespuesta(int creados, int actualizados,
                                                      List<ErrorFilaDTO> errores,
                                                      List<CuentaCreadaDTO> cuentasCreadas) {
        int total = creados + actualizados + errores.size();
        return new CargaMasivaResponseDTO(total, creados, actualizados, errores.size(), errores, cuentasCreadas);
    }

    // Record interno para encapsular el resultado de la operación
    private record ResultadoOperacion(boolean esNuevo, Usuario usuario) {}
}