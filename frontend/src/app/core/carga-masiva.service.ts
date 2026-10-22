import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { CARGA_CSV_URL, CargaMasivaResponseDTO, ErrorHttpCsv } from './api';

@Injectable({
  providedIn: 'root',
})
export class CargaMasivaService {
  private http = inject(HttpClient);

  /** Envía el archivo CSV al backend mediante multipart/form-data. */
  cargarCsv(archivo: File): Observable<CargaMasivaResponseDTO> {
    const formData = new FormData();
    formData.append('archivo', archivo, archivo.name);

    return this.http.post<CargaMasivaResponseDTO>(CARGA_CSV_URL, formData).pipe(
      catchError((res: HttpErrorResponse) => {
        let detalle: ErrorHttpCsv = {
          error: 'Error de servidor',
          mensaje: 'Ocurrió un error inesperado al procesar el archivo.',
        };

        if (res.error && typeof res.error === 'object') {
          detalle = {
            error: res.error.error || 'Error al procesar archivo',
            mensaje: res.error.mensaje || res.message || 'No se pudo procesar el archivo CSV.',
            numeroFila: res.error.numeroFila,
          };
        } else if (typeof res.error === 'string' && res.error.trim().length > 0) {
          detalle.mensaje = res.error;
        } else if (res.status === 0) {
          detalle = {
            error: 'Sin conexión',
            mensaje: 'No se pudo conectar con el servidor backend (puerto 8080). Verifica que el backend esté ejecutándose.',
          };
        }

        return throwError(() => detalle);
      })
    );
  }
}
