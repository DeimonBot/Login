package co.edu.unipamplona.procesosoftware.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Una cuenta de usuario. Cada fila del CSV se convierte en uno de estos.
 * La tabla "usuarios" la crea Hibernate solito al arrancar.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    /** La cédula identifica a la persona: si ya existe, el CSV ACTUALIZA en vez de crear. */
    @Column(nullable = false, unique = true)
    private String cedula;

    /** Con el correo se inicia sesión. */
    @Column(nullable = false, unique = true)
    private String correo;

    private String telefono;

    private String carrera;

    /**
     * ESTUDIANTE o DOCENTE. Si no se indica, la cuenta queda como ESTUDIANTE
     * (así las cuentas que cree el CSV son estudiantes por defecto).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.ESTUDIANTE;

    /**
     * Contraseña en texto plano (el profe dijo "sin encriptar por ahora").
     * Al crear la cuenta desde el CSV se usa la cédula como contraseña inicial.
     * @JsonIgnore evita que la contraseña salga en las respuestas JSON.
     */
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    public Usuario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
