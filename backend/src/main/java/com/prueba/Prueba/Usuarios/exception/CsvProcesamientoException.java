package com.prueba.Prueba.Usuarios.exception;

public class CsvProcesamientoException extends RuntimeException {

    public CsvProcesamientoException(String mensaje, Throwable causa) {
        super("Error procesando CSV: " + mensaje, causa);
    }
}