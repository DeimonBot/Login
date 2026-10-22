package com.prueba.Prueba.Usuarios.exception;

public class CsvArchivoInvalidoException extends RuntimeException {

    public CsvArchivoInvalidoException(String mensaje) {
        super("Archivo CSV inválido: " + mensaje);
    }
}