package com.prueba.Prueba.Usuarios.dtos;

public record ErrorFilaDTO(
        int numeroFila,
        String campo,
        String mensaje
) {}