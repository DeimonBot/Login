import { Component, input, signal } from '@angular/core';

/**
 * Escudo + nombre de la universidad, pensado para fondos oscuros (rojo o fotos).
 * Carga public/img/escudo.png; si el archivo no existe, muestra el monograma "UP".
 *   tamano="grande"  -> portada del login
 *   tamano="pequeno" -> barra de navegación
 */
@Component({
  selector: 'app-escudo',
  template: `
    <span class="escudo" [class.pequeno]="tamano() === 'pequeno'">
      @if (!fallo()) {
        <img src="img/escudo.png" alt="Escudo de la Universidad de Pamplona" (error)="fallo.set(true)" />
      } @else {
        <span class="monograma" aria-hidden="true">UP</span>
      }
      <span class="nombre">
        <span class="de">Universidad de</span>
        <span class="pamplona">Pamplona</span>
      </span>
    </span>
  `,
  styles: `
    .escudo {
      display: inline-flex;
      align-items: center;
      gap: 16px;
      color: #fff;
    }
    img {
      height: 112px;
      width: auto;
      display: block;
      filter: drop-shadow(0 6px 16px rgb(30 6 6 / 0.45));
    }
    .monograma {
      display: grid;
      place-items: center;
      width: 84px;
      height: 84px;
      border-radius: 50%;
      background: #fff;
      color: var(--primario);
      font-weight: 800;
      font-size: 1.5rem;
    }
    .nombre {
      display: flex;
      flex-direction: column;
      line-height: 1.05;
      text-shadow: 0 1px 10px rgb(40 8 8 / 0.4);
    }
    .de {
      font-size: 0.95rem;
      font-weight: 500;
      opacity: 0.9;
    }
    .pamplona {
      font-size: 1.6rem;
      font-weight: 800;
      letter-spacing: -0.01em;
    }

    .pequeno {
      gap: 10px;
    }
    .pequeno img {
      height: 46px;
      filter: none;
    }
    .pequeno .monograma {
      width: 38px;
      height: 38px;
      font-size: 0.8rem;
    }
    .pequeno .nombre {
      text-shadow: none;
    }
    .pequeno .de {
      font-size: 0.7rem;
    }
    .pequeno .pamplona {
      font-size: 1rem;
    }

    @media (max-width: 860px) {
      .escudo:not(.pequeno) img {
        height: 76px;
      }
      .escudo:not(.pequeno) .monograma {
        width: 60px;
        height: 60px;
        font-size: 1.1rem;
      }
      .escudo:not(.pequeno) .pamplona {
        font-size: 1.3rem;
      }
      .escudo:not(.pequeno) .de {
        font-size: 0.82rem;
      }
    }
  `,
})
export class Escudo {
  readonly tamano = input<'grande' | 'pequeno'>('grande');
  readonly fallo = signal(false);
}
