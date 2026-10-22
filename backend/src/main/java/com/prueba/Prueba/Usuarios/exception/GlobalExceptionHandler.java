package com.prueba.Prueba.Usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CsvArchivoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCsvArchivoInvalido(CsvArchivoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Archivo CSV inválido",
                        "mensaje", ex.getMessage()
                ));
    }

    @ExceptionHandler(CsvFilaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleCsvFilaInvalida(CsvFilaInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Fila inválida en CSV",
                        "numeroFila", ex.getNumeroFila(),
                        "mensaje", ex.getMessage()
                ));
    }

    @ExceptionHandler(CsvProcesamientoException.class)
    public ResponseEntity<Map<String, Object>> handleCsvProcesamiento(CsvProcesamientoException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Error procesando CSV",
                        "mensaje", ex.getMessage()
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of(
                        "error", "Archivo demasiado grande",
                        "mensaje", "El archivo supera el tamaño máximo permitido"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Error interno del servidor",
                        "mensaje", ex.getMessage() != null ? ex.getMessage() : "Error desconocido"
                ));
    }
}