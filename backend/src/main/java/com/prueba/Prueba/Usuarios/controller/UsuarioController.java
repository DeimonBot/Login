package com.prueba.Prueba.Usuarios.controller;

import com.prueba.Prueba.Usuarios.dtos.CargaMasivaResponseDTO;
import com.prueba.Prueba.Usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping(value = "/cargar-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CargaMasivaResponseDTO> cargarEstudiantesCsv(
            @RequestParam("archivo") MultipartFile archivo) {

        CargaMasivaResponseDTO respuesta = usuarioService.cargarEstudiantesDesdeCsv(archivo);
        return ResponseEntity.ok(respuesta);
    }
}