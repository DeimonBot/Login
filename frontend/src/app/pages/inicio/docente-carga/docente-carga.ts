import { Component, computed, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CargaMasivaResponseDTO, ErrorHttpCsv, UsuarioSesion } from '../../../core/api';
import { CargaMasivaService } from '../../../core/carga-masiva.service';

@Component({
  selector: 'app-docente-carga',
  imports: [CommonModule, FormsModule],
  templateUrl: './docente-carga.html',
  styleUrl: './docente-carga.css',
})
export class DocenteCarga {
  private cargaMasivaService = inject(CargaMasivaService);

  readonly docente = input.required<UsuarioSesion>();

  // Estado del archivo
  readonly archivoSeleccionado = signal<File | null>(null);
  readonly estaCargando = signal<boolean>(false);

  // Estado de resultados
  readonly respuesta = signal<CargaMasivaResponseDTO | null>(null);
  readonly pestanaActiva = signal<'cuentas' | 'errores'>('cuentas');
  readonly filtroBusqueda = signal<string>('');
  readonly passwordCopiada = signal<string | null>(null);
  readonly ejemploCopiado = signal<boolean>(false);

  // Modal para errores de archivo / extensión
  readonly modalError = signal<{ visible: boolean; titulo: string; mensaje: string }>({
    visible: false,
    titulo: '',
    mensaje: '',
  });

  // Líneas del archivo CSV de ejemplo tipo bloque de código
  readonly lineasCsv = [
    { num: 1, texto: 'nombre,apellido,cc,correo,telefono,carrera', esHeader: true },
    { num: 2, texto: 'Juan,Perez,1098765432,juan.perez@unipamplona.edu.co,3101234567,Ingeniería de Sistemas', esHeader: false },
    { num: 3, texto: 'Maria,Gomez,1087654321,maria.gomez@unipamplona.edu.co,3201234567,Ingeniería Civil', esHeader: false },
    { num: 4, texto: 'Carlos,Ruiz,1076543210,carlos.ruiz@unipamplona.edu.co,3151234567,Administración de Empresas', esHeader: false },
  ];

  // Cuentas filtradas por buscador
  readonly cuentasFiltradas = computed(() => {
    const res = this.respuesta();
    if (!res || !res.cuentasCreadas) {
      return [];
    }
    const q = this.filtroBusqueda().trim().toLowerCase();
    if (!q) {
      return res.cuentasCreadas;
    }
    return res.cuentasCreadas.filter(
      (c) =>
        c.cedula.toLowerCase().includes(q) ||
        c.correo.toLowerCase().includes(q) ||
        (c.password && c.password.toLowerCase().includes(q))
    );
  });

  /** Maneja la selección del archivo en el input file */
  onArchivoSeleccionado(event: Event): void {
    const inputEl = event.target as HTMLInputElement;
    if (!inputEl.files || inputEl.files.length === 0) {
      return;
    }

    const archivo = inputEl.files[0];

    // Validación de extensión .csv
    if (!archivo.name.toLowerCase().endsWith('.csv')) {
      inputEl.value = '';
      this.archivoSeleccionado.set(null);
      this.abrirModalError(
        'Archivo CSV inválido',
        'Archivo CSV inválido: El archivo debe tener extensión .csv'
      );
      return;
    }

    // Validación de archivo no vacío
    if (archivo.size === 0) {
      inputEl.value = '';
      this.archivoSeleccionado.set(null);
      this.abrirModalError('Archivo CSV inválido', 'El archivo seleccionado está vacío');
      return;
    }

    this.archivoSeleccionado.set(archivo);
  }

  /** Limpia el archivo seleccionado */
  quitarArchivo(inputEl?: HTMLInputElement): void {
    this.archivoSeleccionado.set(null);
    if (inputEl) {
      inputEl.value = '';
    }
  }

  /** Envía el archivo CSV al backend */
  crearCuentas(): void {
    const archivo = this.archivoSeleccionado();
    if (!archivo) {
      this.abrirModalError(
        'Archivo requerido',
        'Por favor selecciona un archivo con extensión .csv antes de continuar.'
      );
      return;
    }

    if (!archivo.name.toLowerCase().endsWith('.csv')) {
      this.abrirModalError(
        'Archivo CSV inválido',
        'Archivo CSV inválido: El archivo debe tener extensión .csv'
      );
      return;
    }

    this.estaCargando.set(true);

    this.cargaMasivaService.cargarCsv(archivo).subscribe({
      next: (res) => {
        this.estaCargando.set(false);
        this.respuesta.set(res);

        // Si solo hubo errores, abrimos la pestaña de errores automáticamente
        if (res.errores > 0 && res.creados === 0) {
          this.pestanaActiva.set('errores');
        } else {
          this.pestanaActiva.set('cuentas');
        }
      },
      error: (err: ErrorHttpCsv) => {
        this.estaCargando.set(false);
        this.abrirModalError(
          err.error || 'Error al procesar archivo',
          err.mensaje || 'No se pudo completar el procesamiento del archivo CSV.'
        );
      },
    });
  }

  abrirModalError(titulo: string, mensaje: string): void {
    this.modalError.set({
      visible: true,
      titulo,
      mensaje,
    });
  }

  cerrarModal(): void {
    this.modalError.set({
      visible: false,
      titulo: '',
      mensaje: '',
    });
  }

  copiarAlPortapapeles(texto: string): void {
    navigator.clipboard?.writeText(texto).then(() => {
      this.passwordCopiada.set(texto);
      setTimeout(() => this.passwordCopiada.set(null), 2000);
    });
  }

  copiarTextoEjemplo(): void {
    const texto = this.lineasCsv.map((l) => l.texto).join('\n');
    navigator.clipboard?.writeText(texto).then(() => {
      this.ejemploCopiado.set(true);
      setTimeout(() => this.ejemploCopiado.set(false), 2000);
    });
  }

  formatearTamano(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    const kb = (bytes / 1024).toFixed(1);
    return kb + ' KB';
  }
}
