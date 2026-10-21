import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { mensajeDeError } from '../../core/errores';
import { Escudo } from '../../shared/escudo/escudo';

/**
 * Fotos del campus para el fondo (están en public/img/).
 * Las que no existan se ignoran; si no hay ninguna, se ve el fondo rojo con contornos.
 * "encuadre" es la posición CSS de la foto: '72% center' corre la foto para mostrar más el lado derecho.
 */
interface FotoCampus {
  src: string;
  encuadre: string;
}

const FOTOS_CAMPUS: FotoCampus[] = [
  { src: 'img/campus-1.jpg', encuadre: 'center' }, // biblioteca de noche
  { src: 'img/campus-2.jpg', encuadre: '72% center' }, // edificio con la fuente
  // Para agregar otra: copien la foto a public/img/ y agreguen una línea como esta:
  // { src: 'img/campus-3.jpg', encuadre: 'center' },
];

/** Cada cuánto cambia la foto (milisegundos). */
const INTERVALO_FOTOS = 5000;

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, Escudo],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);
  readonly verPassword = signal(false);
  /** Si falta img/sello-acreditacion.png se dibuja un sello sencillo en SVG. */
  readonly selloFallo = signal(false);

  /** Fotos que sí cargaron y cuál se está mostrando. */
  readonly fotos = signal<FotoCampus[]>([]);
  readonly fotoActual = signal(0);

  readonly form = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  ngOnInit(): void {
    this.cargarFotos();
  }

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // muestra los mensajes de "campo obligatorio"
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    const { correo, password } = this.form.getRawValue();
    this.auth.login(correo, password).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/inicio']);
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(mensajeDeError(err, 'No se pudo iniciar sesión. Intenta de nuevo.'));
      },
    });
  }

  /** true si el campo ya fue tocado y tiene ese error (para mostrar el mensaje). */
  tieneError(campo: 'correo' | 'password', error: string): boolean {
    const control = this.form.controls[campo];
    return control.touched && control.hasError(error);
  }

  /** Precarga las fotos y solo usa las que existen, en el orden de la lista. */
  private cargarFotos(): void {
    const cargadas: (FotoCampus | null)[] = FOTOS_CAMPUS.map(() => null);
    let pendientes = FOTOS_CAMPUS.length;

    const terminar = () => {
      if (--pendientes > 0) return;
      const lista = cargadas.filter((f): f is FotoCampus => f !== null);
      this.fotos.set(lista);
      this.iniciarCarrusel(lista.length);
    };

    FOTOS_CAMPUS.forEach((foto, i) => {
      const img = new Image();
      img.onload = () => {
        cargadas[i] = foto;
        terminar();
      };
      img.onerror = terminar;
      img.src = foto.src;
    });
  }

  private iniciarCarrusel(cantidad: number): void {
    const sinMovimiento = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
    if (cantidad < 2 || sinMovimiento) return;

    const id = setInterval(() => this.fotoActual.update((i) => (i + 1) % cantidad), INTERVALO_FOTOS);
    this.destroyRef.onDestroy(() => clearInterval(id));
  }
}
