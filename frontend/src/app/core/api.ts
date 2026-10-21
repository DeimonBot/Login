// Dirección del backend de Spring Boot.
// Si cambian el puerto en application.properties, cámbienlo aquí también.
export const API_URL = 'http://localhost:8080/api';

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
