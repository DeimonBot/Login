# Proceso Software · Login

Spring Boot 3.5 + H2 (backend) · Angular 21 (frontend). Sin encriptar, como pidió el profe.
Parte de **Dana y Natanael**. La carga de cuentas desde CSV la hace el resto del grupo.

```
Angular (pantalla de login)
   │  POST /api/auth/login  { correo, password }
   ▼
Spring Boot ──► busca el correo en H2 y compara la contraseña
   │
   ├─ 200 { id, nombre, correo, carrera, rol } ──► Angular guarda la sesión, entra a /inicio
   │                                              y muestra "Ingresaste como estudiante/docente"
   └─ 401 { mensaje }                     ──► Angular muestra "Correo o contraseña incorrectos."
```

---

## 1. Qué tienen que tener instalado

| Herramienta | Versión | Cómo verificar | Dónde se baja |
|---|---|---|---|
| JDK (Java) | 17 o más | `java -version` | adoptium.net (Temurin 21) |
| Maven | 3.9+ (opcional si usan IntelliJ, que ya lo trae) | `mvn -v` | maven.apache.org |
| Node.js | 20.19+, 22.12+ o 24 | `node -v` | nodejs.org (versión LTS) |
| Editor | IntelliJ IDEA Community **o** VS Code con *Extension Pack for Java* | — | — |
| Git | cualquiera reciente | `git --version` | git-scm.com |

No hace falta instalar Angular CLI ni ninguna base de datos: H2 viene dentro del backend.

---

## 2. Paso a paso para correrlo

### Paso 1 — Backend (Spring Boot)

**IntelliJ:** `File → Open…` → carpeta **`backend`** → esperar a que descargue dependencias →
abrir `ProcesoSoftwareApplication.java` → ▶ verde al lado de `main`.

**Terminal:**
```bash
cd backend
mvn spring-boot:run
```
✅ En la consola sale `Tomcat started on port 8080`.

### Paso 2 — Frontend (Angular)

En **otra** terminal:
```bash
cd frontend
npm install      # solo la primera vez
npm start
```
✅ Abrir <http://localhost:4200> → sale la pantalla de login.

### Paso 3 — Probar

Al arrancar, el backend crea **dos cuentas de prueba**, una de cada rol (archivo `datos/DatosDePrueba.java`):

| Rol | Correo | Contraseña |
|---|---|---|
| Estudiante | `ana.perez@unipamplona.edu.co` | `1090123456` |
| Docente | `carlos.rodriguez@unipamplona.edu.co` | `1094555222` |

| Prueba | Resultado esperado |
|---|---|
| Dar "Entrar" con los campos vacíos | Mensajes rojos debajo de cada campo |
| Correo bien, contraseña mala | "Correo o contraseña incorrectos." |
| Entrar con Ana | `/inicio` dice "Ingresaste como **estudiante**" (aviso rojo) |
| Entrar con Carlos | `/inicio` dice "Ingresaste como **docente**" (aviso azul) |
| Recargar la página estando adentro | Sigue con la sesión |
| "Cerrar sesión" | Vuelve al login |
| Escribir `localhost:4200/inicio` sin sesión | Lo devuelve al login |

**Probar el backend solo:** abrir `backend/pruebas.http` en IntelliJ o VS Code (extensión *REST Client*) → *Send Request*.

**Ver la base de datos:** <http://localhost:8080/h2-console> → JDBC URL `jdbc:h2:mem:procesodb`, usuario `sa`, sin contraseña → `SELECT * FROM USUARIOS;`

---

## 3. Endpoint

| Método | Ruta | Envía | Responde |
|---|---|---|---|
| `POST` | `/api/auth/login` | JSON `{ "correo", "password" }` | `200` `{ id, nombre, correo, carrera, rol }` (rol = `"ESTUDIANTE"` o `"DOCENTE"`) · `401` `{ "mensaje": "Correo o contraseña incorrectos." }` |

---

## 4. Qué hace cada archivo

### Backend (`backend/src/main/java/co/edu/unipamplona/procesosoftware/`)

| Archivo | Qué hace |
|---|---|
| `auth/AuthController.java` | El endpoint `/api/auth/login` |
| `usuario/Usuario.java` | La tabla `usuarios`. **La comparte con el equipo del CSV** |
| `usuario/Rol.java` | Los roles: `ESTUDIANTE` y `DOCENTE` |
| `usuario/UsuarioRepository.java` | Consultas a la BD (`findByCorreoIgnoreCase`, `findByCedula`). **La comparte con el equipo del CSV** |
| `datos/DatosDePrueba.java` | Crea 2 cuentas al arrancar. **Se borra cuando se integre el CSV** |
| `config/CorsConfig.java` | Deja que Angular (puerto 4200) le hable a Spring (8080) |
| `resources/application.properties` | Configuración de H2 y del servidor |

### Frontend (`frontend/src/app/`)

| Archivo | Qué hace |
|---|---|
| `core/auth.service.ts` | Llama al backend, guarda quién inició sesión y cierra sesión |
| `core/auth.guard.ts` | Bloquea las rutas privadas si no hay sesión |
| `core/api.ts` | URL del backend, el tipo `Rol` y el tipo `UsuarioSesion` |
| `shared/escudo/` | Escudo + nombre de la universidad (grande en el login, pequeño en la barra) |
| `core/errores.ts` | Convierte errores HTTP en mensajes entendibles |
| `pages/login/` | Pantalla de login (formulario reactivo con validaciones) |
| `pages/inicio/` | Pantalla después de entrar: "Ingresaste como estudiante/docente" + cerrar sesión |
| `app.routes.ts` | Rutas: `/login` (pública) e `/inicio` (protegida) |

---

## 5. Lo que hay que acordar con el equipo del CSV

| Tema | Cómo lo espera el login |
|---|---|
| Entidad | Usar la misma `Usuario.java`: `nombre, cedula, correo, telefono, carrera, rol, password` |
| Rol | Si no le ponen rol, la cuenta queda como `ESTUDIANTE`. Para crear docentes desde el CSV: agregar una columna `rol` y hacer `u.setRol(Rol.DOCENTE)` |
| Correo | Único y guardado en minúsculas (el login busca sin distinguir mayúsculas) |
| Contraseña | El CSV no trae contraseña, así que la inicial es la **cédula** (`u.setPassword(cedula)`) |
| Al integrar | Ellos agregan su servicio y controlador; se borra `DatosDePrueba.java` |
| Para proteger otras pantallas | En `app.routes.ts`: `{ path: 'x', component: X, canActivate: [authGuard] }` |

---

## 6. Problemas comunes

| Síntoma | Causa | Solución |
|---|---|---|
| *"No hay conexión con el servidor"* en el login | El backend no está corriendo | Paso 1 |
| Error de **CORS** en la consola del navegador | Angular corre en un puerto distinto de 4200 | Agregar ese origen en `CorsConfig.java` |
| `Port 8080 was already in use` | Otro programa usa el puerto | Cerrarlo o cambiar `server.port` (y `API_URL` en `core/api.ts`) |
| `npm install` falla con *"reading 'edgesOut'"* | Bug de npm 10 | `npx npm@11 install` |
| *"The Angular CLI requires a minimum Node.js version"* | Node viejo | Instalar Node LTS |
| `release version 17 not supported` | JDK más viejo que 17 | Instalar JDK 17 o 21 |

---

## 7. Trabajar con Git (para el equipo)

### Bajar el proyecto por primera vez
```bash
git clone <URL-del-repositorio>
cd <carpeta-del-repositorio>
cd frontend
npm install
```
Luego seguir la sección 2 (backend en IntelliJ abriendo la carpeta `backend`, y `npm start` en `frontend`).

> `node_modules`, `target`, `dist` y `.angular` **no se suben** al repo (están en `.gitignore`).
> Por eso, después de clonar, siempre hay que correr `npm install` en `frontend`.

### Día a día
| Quiero… | Comando |
|---|---|
| Traer lo último que subieron los demás | `git pull` |
| Ver qué archivos cambié | `git status` |
| Guardar mis cambios | `git add .` y luego `git commit -m "Qué hice"` |
| Subirlos | `git push` |

Antes de empezar a trabajar: `git pull`. Antes de subir: `git pull` y luego `git push`.

