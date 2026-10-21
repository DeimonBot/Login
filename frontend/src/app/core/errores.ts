import { HttpErrorResponse } from '@angular/common/http';

/** Convierte un error HTTP en un mensaje entendible para mostrar en pantalla. */
export function mensajeDeError(err: unknown, porDefecto = 'Ocurrió un error inesperado.'): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'No hay conexión con el servidor. ¿Está corriendo el backend en el puerto 8080?';
    }
    const mensaje = (err.error as { mensaje?: string } | null)?.mensaje;
    if (mensaje) {
      return mensaje;
    }
  }
  return porDefecto;
}
