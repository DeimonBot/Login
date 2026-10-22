package com.prueba.Prueba.Usuarios.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EstudianteCsv(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 20, message = "El nombre no puede superar los 20 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 20, message = "El apellido no puede superar los 20 caracteres")
        String apellido,

        @NotBlank(message = "La cedula es obligatorio")
        @Size(max = 10, message = "La cedula no puede superar los 10 caracteres")
        String cc,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El formato del correo no es valido")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@unipamplona\\.edu\\.co$",
                message = "Debe utilizar un correo universitario"
        )
        String correo,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(min = 10, max = 10, message = "El telefono debe tener 10 caracteres")
        String telefono,

        @NotBlank(message = "La carrera es obligatorio")
        @Size(max = 50, message = "La carrera no puede superar los 50 caracteres")
        String carrera
) {
}
