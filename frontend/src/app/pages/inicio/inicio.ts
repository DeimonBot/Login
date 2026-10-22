import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { NOMBRE_ROL } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Escudo } from '../../shared/escudo/escudo';
import { DocenteCarga } from './docente-carga/docente-carga';

/** Página que se ve después de iniciar sesión (la ruta está protegida con authGuard). */
@Component({
  selector: 'app-inicio',
  imports: [Escudo, DocenteCarga],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
  private auth = inject(AuthService);
  private router = inject(Router);

  readonly usuario = this.auth.usuario;

  /** "estudiante" o "docente", para mostrar en pantalla. */
  readonly nombreRol = computed(() => {
    const u = this.usuario();
    return u ? NOMBRE_ROL[u.rol] : '';
  });

  readonly esDocente = computed(() => this.usuario()?.rol === 'DOCENTE');

  salir(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
