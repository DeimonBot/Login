package co.edu.unipamplona.procesosoftware.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring genera la implementación solito a partir del nombre de los métodos.
 * findByCorreoIgnoreCase -> SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(?)
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCedula(String cedula);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
