package com.prueba.Prueba.Usuarios.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.prueba.Prueba.Usuarios.dtos.EstudianteCsv;
import com.prueba.Prueba.Usuarios.exception.CsvFilaInvalidaException;
import com.prueba.Prueba.Usuarios.exception.CsvProcesamientoException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    public List<EstudianteCsv> parsearArchivo(MultipartFile archivo) {
        List<EstudianteCsv> estudiantes = new ArrayList<>();
        int numeroFila = 1;

        try (BufferedReader reader = crearReader(archivo);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] linea;
            while ((linea = csvReader.readNext()) != null) {
                numeroFila++;
                estudiantes.add(parsearFila(linea, numeroFila));
            }

        } catch (IOException e) {
            throw new CsvProcesamientoException("Error de lectura del archivo", e);
        } catch (CsvValidationException e) {
            throw new CsvProcesamientoException("Error de validación CSV", e);
        }

        return estudiantes;
    }

    private BufferedReader crearReader(MultipartFile archivo) throws IOException {
        return new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    private EstudianteCsv parsearFila(String[] linea, int numeroFila) {
        validarColumnas(linea, numeroFila);

        return new EstudianteCsv(
                linea[0].trim(),
                linea[1].trim(),
                linea[2].trim(),
                linea[3].trim(),
                linea[4].trim(),
                linea[5].trim()
        );
    }

    private void validarColumnas(String[] linea, int numeroFila) {
        if (linea.length < 6) {
            throw new CsvFilaInvalidaException(
                    numeroFila,
                    "La fila debe tener 6 columnas, pero tiene " + linea.length
            );
        }
    }
}