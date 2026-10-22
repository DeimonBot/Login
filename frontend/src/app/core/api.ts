// Dirección del backend de Spring Boot.
// Si cambian el puerto en application.properties, cámbienlo aquí también.
export const API_URL = 'http://localhost:8080/api';
export const CARGA_CSV_URL = `${API_URL}/v1/usuarios/cargar-csv`;

/** Tipos de cuenta (iguales al enum Rol.java del backend). */
export type Rol = 'ESTUDIANTE' | 'DOCENTE';

/** Cómo se muestra cada rol en pantalla. */
export const NOMBRE_ROL: Record<Rol, string> = {
  ESTUDIANTE: 'estudiante',
  DOCENTE: 'docente',
};

/** Lo que devuelve el backend cuando el login sale bien. */
export interface UsuarioSesion {
  id: number;
  nombre: string;
  correo: string;
  carrera: string;
  rol: Rol;
}

/** Error individual en una fila del archivo CSV. */
export interface ErrorFilaDTO {
  numeroFila: number;
  campo: string;
  mensaje: string;
}

/** Cuenta creada devuelta en la respuesta de carga masiva. */
export interface CuentaCreadaDTO {
  cedula: string;
  correo: string;
  password?: string;
}

/** Respuesta completa de carga masiva desde el backend. */
export interface CargaMasivaResponseDTO {
  totalProcesados: number;
  creados: number;
  actualizados: number;
  errores: number;
  erroresDetalle: ErrorFilaDTO[];
  cuentasCreadas?: CuentaCreadaDTO[];
}

/** Error HTTP devuelto por el backend al validar el archivo. */
export interface ErrorHttpCsv {
  error: string;
  mensaje: string;
  numeroFila?: number;
}
