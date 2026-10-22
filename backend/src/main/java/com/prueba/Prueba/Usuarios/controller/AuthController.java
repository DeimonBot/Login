package com.prueba.Prueba.Usuarios.controller;


import com.prueba.Prueba.Usuarios.entitys.Rol;
import com.prueba.Prueba.Usuarios.entitys.Usuario;
import com.prueba.Prueba.Usuarios.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    public record LoginRequest(@NotBlank String correo, @NotBlank String password) {
    }

    public record LoginResponse(Long id, String nombre, String correo, String carrera, Rol rol) {
    }

    private final UsuarioRepository repo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return repo.findByCorreoIgnoreCase(req.correo().trim())
                .filter(u -> u.getPassword().equals(req.password()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(aRespuesta(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("mensaje", "Correo o contraseña incorrectos.")));
    }

    private static LoginResponse aRespuesta(Usuario u) {
        return new LoginResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getCarrera(), u.getRol());
    }
}
