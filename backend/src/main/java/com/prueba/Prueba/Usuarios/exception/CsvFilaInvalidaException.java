package com.prueba.Prueba.Usuarios.exception;

public class CsvFilaInvalidaException extends RuntimeException {

    private final int numeroFila;

    public CsvFilaInvalidaException(int numeroFila, String mensaje) {
        super("Fila " + numeroFila + ": " + mensaje);
        this.numeroFila = numeroFila;
    }

    public int getNumeroFila() {
        return numeroFila;
    }
}