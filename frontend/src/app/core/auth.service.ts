import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { API_URL, UsuarioSesion } from './api';

const CLAVE_SESION = 'usuarioSesion';

/**
 * Maneja el inicio y cierre de sesión.
 * Guarda el usuario logueado en sessionStorage para que no se pierda al recargar la página.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  /** Usuario logueado (o null si no hay sesión). */
  readonly usuario = signal<UsuarioSesion | null>(this.leerSesionGuardada());
  readonly estaLogueado = computed(() => this.usuario() !== null);

  login(correo: string, password: string): Observable<UsuarioSesion> {
    return this.http
      .post<UsuarioSesion>(`${API_URL}/auth/login`, { correo, password })
      .pipe(
        tap((usuario) => {
          this.usuario.set(usuario);
          try {
            sessionStorage.setItem(CLAVE_SESION, JSON.stringify(usuario));
          } catch {
            /* si el navegador bloquea el storage, la sesión solo dura hasta recargar */
          }
        }),
      );
  }

  logout(): void {
    this.usuario.set(null);
    try {
      sessionStorage.removeItem(CLAVE_SESION);
    } catch {
      /* nada */
    }
  }

  private leerSesionGuardada(): UsuarioSesion | null {
    try {
      const guardado = sessionStorage.getItem(CLAVE_SESION);
      const usuario = guardado ? (JSON.parse(guardado) as UsuarioSesion) : null;
      return usuario?.rol ? usuario : null; // sesiones viejas sin rol: pedir login otra vez
    } catch {
      return null;
    }
  }
}
