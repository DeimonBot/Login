package com.prueba.Prueba.Usuarios.data;

import com.prueba.Prueba.Usuarios.entitys.Rol;
import com.prueba.Prueba.Usuarios.entitys.Usuario;
import com.prueba.Prueba.Usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatosDePrueba implements CommandLineRunner {

    private final UsuarioRepository repo;
    @Override
    public void run(String... args) {
        crearSiNoExiste("Ana", "Perez", "1090123456", "ana.perez@unipamplona.edu.co",
                "3001234567", "Derecho", Rol.ESTUDIANTE);
        crearSiNoExiste("Carlos", "Rodríguez", "1094555222", "carlos.rodriguez@unipamplona.edu.co",
                "3109876543", "Derecho", Rol.DOCENTE);
    }

    private void crearSiNoExiste(String nombre,String apellido, String cedula, String correo, String telefono, String carrera,
                                 Rol rol) {
        if (repo.findByCorreoIgnoreCase(correo).isPresent()) {
            return;
        }
        String password = nombre.toUpperCase().charAt(0) + cedula + "*";
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCedula(cedula);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setCarrera(carrera);
        u.setRol(rol);
        u.setPassword(password);
        repo.save(u);
    }


}
