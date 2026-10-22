package com.prueba.Prueba.Usuarios.dtos;

import java.util.List;

public record CargaMasivaResponseDTO(
        int totalProcesados,
        int creados,
        int actualizados,
        int errores,
        List<ErrorFilaDTO> erroresDetalle,
        List<CuentaCreadaDTO> cuentasCreadas
) {}