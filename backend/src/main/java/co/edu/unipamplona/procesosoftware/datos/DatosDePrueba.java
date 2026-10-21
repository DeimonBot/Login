package co.edu.unipamplona.procesosoftware.datos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.unipamplona.procesosoftware.usuario.Rol;
import co.edu.unipamplona.procesosoftware.usuario.Usuario;
import co.edu.unipamplona.procesosoftware.usuario.UsuarioRepository;

/**
 * Crea dos cuentas de prueba al arrancar (una de cada rol), para poder probar el login
 * mientras el otro equipo termina la carga desde CSV.
 *
 * Cuando junten las dos partes, BORREN este archivo: las cuentas las va a crear el CSV.
 */
@Component
public class DatosDePrueba implements CommandLineRunner {

    private final UsuarioRepository repo;

    public DatosDePrueba(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste("Ana María Pérez", "1090123456", "ana.perez@unipamplona.edu.co",
                "3001234567", "Derecho", Rol.ESTUDIANTE);
        crearSiNoExiste("Carlos Rodríguez", "1094555222", "carlos.rodriguez@unipamplona.edu.co",
                "3109876543", "Derecho", Rol.DOCENTE);
    }

    private void crearSiNoExiste(String nombre, String cedula, String correo, String telefono, String carrera,
            Rol rol) {
        if (repo.findByCorreoIgnoreCase(correo).isPresent()) {
            return;
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCedula(cedula);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setCarrera(carrera);
        u.setRol(rol);
        u.setPassword(cedula); // misma regla que usará el CSV: contraseña inicial = cédula
        repo.save(u);
    }
}
