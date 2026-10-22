package com.prueba.Prueba.Usuarios.repository;

import com.prueba.Prueba.Usuarios.entitys.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByCedula(String cc);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
