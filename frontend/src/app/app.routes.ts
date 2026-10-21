import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';
import { Inicio } from './pages/inicio/inicio';
import { Login } from './pages/login/login';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'inicio', component: Inicio, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];
