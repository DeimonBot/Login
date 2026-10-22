# Proceso Software · Login y Carga Masiva de Usuarios

Sistema web desarrollado con **Spring Boot (backend)** y **Angular (frontend)**, que integra el módulo de autenticación (Login) y la carga masiva de estudiantes mediante archivos CSV en base de datos en memoria **H2**.

---

## 📐 Arquitectura y Flujos del Sistema

### 1. Autenticación (Login)
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

### 2. Carga Masiva de Estudiantes (CSV)
```
Cliente / Postman / Frontend
   │  POST /api/v1/usuarios/cargar-csv (multipart/form-data con archivo .csv)
   ▼
Spring Boot (UsuarioController ──► CsvParserService ──► UsuarioService)
   │
   ├─ Valida formato y columnas del archivo CSV (OpenCSV)
   ├─ Valida reglas de negocio por fila (correo institucional, longitudes, etc.)
   ├─ Si el estudiante ya existe (por cédula) ──► Actualiza sus datos
   ├─ Si no existe ──► Crea el usuario (Rol ESTUDIANTE y contraseña: [Inicial][Cédula]*)
   └─ Retorna 200 { totalProcesados, creados, actualizados, errores, erroresDetalle }
```

---

## 1. Requisitos Previos

| Herramienta | Versión recomendada | Cómo verificar | Dónde se descarga |
|---|---|---|---|
| **JDK (Java)** | **21** | `java -version` | [adoptium.net](https://adoptium.net/) (Temurin 21) |
| **Maven Wrapper** | Incluido en el repo (`./mvnw`) | `./mvnw -v` | No requiere instalación |
| **Node.js** | 20.19+, 22.12+ o 24 (LTS) | `node -v` | [nodejs.org](https://nodejs.org/) |
| **Editor / IDE** | IntelliJ IDEA Community **o** VS Code | — | — |
| **Git** | Versión reciente | `git --version` | [git-scm.com](https://git-scm.com/) |

> [!NOTE]
> No es necesario instalar una base de datos externa ni Maven global: H2 corre en memoria y el backend incluye el Maven Wrapper (`mvnw`).

---

## 2. Paso a Paso para Ejecutar el Proyecto

### Paso 1 — Backend (Spring Boot)

**Opción A — IntelliJ IDEA:**
1. `File → Open…` → seleccionar la carpeta **`backend`**.
2. Esperar a que descargue dependencias Maven.
3. Abrir `PruebaUsuariosApplication.java` (`com.prueba.Prueba.Usuarios`).
4. Clic en el botón ▶ (*Run*) al lado del método `main`.

**Opción B — Terminal:**
```bash
cd backend

# En Linux / macOS:
chmod +x mvnw
./mvnw spring-boot:run

# En Windows (CMD o PowerShell):
mvnw.cmd spring-boot:run
```
✅ La consola mostrará que el servidor inició en el puerto **8080**.

---

### Paso 2 — Frontend (Angular)

En **otra** terminal:
```bash
cd frontend
npm install      # Solo la primera vez
npm start
```
✅ Abrir en el navegador: <http://localhost:4200> para ver la pantalla de login.

---

### Paso 3 — Probar el Sistema

#### Cuentas de prueba predeterminadas (`DatosDePrueba.java`)

Al arrancar, el backend registra dos usuarios semilla. La contraseña por defecto sigue el patrón:  
**`[Primera letra del nombre en mayúscula][Cédula]*`**

| Rol | Nombre | Correo | Contraseña |
|---|---|---|---|
| **Estudiante** | Ana Perez | `ana.perez@unipamplona.edu.co` | `A1090123456*` |
| **Docente** | Carlos Rodríguez | `carlos.rodriguez@unipamplona.edu.co` | `C1094555222*` |

#### Casos de prueba en el Login:
| Prueba | Resultado esperado |
|---|---|
| Enviar formulario vacío | Mensajes de validación en rojo debajo de cada campo |
| Correo válido, contraseña incorrecta | Mensaje: *"Correo o contraseña incorrectos."* |
| Iniciar sesión con Ana | Redirección a `/inicio` indicando rol **estudiante** |
| Iniciar sesión con Carlos | Redirección a `/inicio` indicando rol **docente** |
| Recargar la página | Mantiene la sesión activa |
| Cerrar sesión | Limpia el almacenamiento y redirige al login |
| Acceder a `/inicio` sin autenticación | Bloqueado por el guard y redirigido a `/login` |

#### Base de Datos H2:
- Consola web: <http://localhost:8080/h2-console>
- **JDBC URL:** `jdbc:h2:mem:trabajoSocial`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*
- Consulta de verificación: `SELECT * FROM USUARIOS;`

---

## 3. Endpoints del API REST

### 1. Autenticación (`AuthController`)

- **Ruta:** `POST /api/auth/login`
- **Headers:** `Content-Type: application/json`
- **Cuerpo de la petición:**
```json
{
  "correo": "ana.perez@unipamplona.edu.co",
  "password": "A1090123456*"
}
```
- **Respuesta Exitosa (`200 OK`):**
```json
{
  "id": 1,
  "nombre": "Ana",
  "correo": "ana.perez@unipamplona.edu.co",
  "carrera": "Derecho",
  "rol": "ESTUDIANTE"
}
```
- **Respuesta de Error (`401 Unauthorized`):**
```json
{
  "mensaje": "Correo o contraseña incorrectos."
}
```

---

### 2. Carga Masiva de Estudiantes (`UsuarioController`)

- **Ruta:** `POST /api/v1/usuarios/cargar-csv`
- **Headers:** `Content-Type: multipart/form-data`
- **Parámetro del form-data:**
  - `archivo`: Archivo con extensión `.csv` (ejemplo disponible en la raíz del proyecto: `datos.csv`).
- **Respuesta Exitosa (`200 OK`):**
```json
{
  "totalProcesados": 30,
  "creados": 30,
  "actualizados": 0,
  "errores": 0,
  "erroresDetalle": []
}
```
- **Si se presentan filas con errores de validación:**
```json
{
  "totalProcesados": 3,
  "creados": 2,
  "actualizados": 0,
  "errores": 1,
  "erroresDetalle": [
    {
      "numeroFila": 3,
      "campo": "validacion",
      "mensaje": "correo: Debe utilizar un correo universitario"
    }
  ]
}
```

---

## 4. Estructura y Reglas del Archivo CSV

El archivo CSV debe tener codificación UTF-8, encabezado en la primera línea y contener 6 columnas obligatorias:

```csv
nombre,apellido,cc,correo,telefono,carrera
Juan,Perez,1098765432,juan.perez@unipamplona.edu.co,3101234567,Ingeniería de Sistemas
Maria,Gomez,1087654321,maria.gomez@unipamplona.edu.co,3201234567,Ingeniería Civil
```

### Validaciones aplicadas por fila:
- **`nombre`**: Obligatorio, máximo 20 caracteres.
- **`apellido`**: Obligatorio, máximo 20 caracteres.
- **`cc` (cédula)**: Obligatoria, máximo 10 caracteres, única.
- **`correo`**: Obligatorio, formato válido con dominio institucional obligatorio (`@unipamplona.edu.co`).
- **`telefono`**: Obligatorio, exactamente 10 dígitos.
- **`carrera`**: Obligatoria, máximo 50 caracteres.

### Reglas de negocio en la carga:
1. **Actualización automática**: Si la cédula ya existe en la base de datos, se actualizan sus datos personales (`nombre`, `apellido`, `correo`, `telefono`, `carrera`).
2. **Creación de nuevos usuarios**: Si la cédula no existe, se registra con el rol `ESTUDIANTE` y contraseña generada automáticamente (`[Inicial][Cédula]*`).
3. **Manejo de errores por fila**: Una fila con error no interrumpe el procesamiento del resto del archivo; se reporta detalladamente en `erroresDetalle`.

---

## 5. Estructura del Proyecto

### Backend (`backend/src/main/java/com/prueba/Prueba/Usuarios/`)

| Paquete / Archivo | Responsabilidad |
|---|---|
| `PruebaUsuariosApplication.java` | Clase principal que inicia el backend Spring Boot |
| `config/CorsConfig.java` | Configuración de CORS para permitir peticiones desde `http://localhost:4200` |
| `controller/AuthController.java` | Endpoint `/api/auth/login` para autenticación |
| `controller/UsuarioController.java` | Endpoint `/api/v1/usuarios/cargar-csv` para carga masiva |
| `service/CsvParserService.java` | Lectura y parseo del CSV usando OpenCSV |
| `service/UsuarioService.java` | Validación de datos, guardado y actualización de estudiantes |
| `dtos/EstudianteCsv.java` | DTO con validaciones Jakarta (`@Email`, `@Pattern`, `@Size`) |
| `dtos/CargaMasivaResponseDTO.java` | Estructura de respuesta resumen de la carga masiva |
| `dtos/ErrorFilaDTO.java` | Estructura de reporte de errores específicos por fila |
| `entitys/Usuario.java` | Entidad JPA mapeada a la tabla `usuarios` |
| `entitys/Rol.java` | Enumeración de roles (`ESTUDIANTE`, `DOCENTE`) |
| `repository/UsuarioRepository.java` | Métodos de consulta (`findByCorreoIgnoreCase`, `findByCedula`) |
| `exception/GlobalExceptionHandler.java` | Manejo centralizado de excepciones y respuestas HTTP de error |
| `data/DatosDePrueba.java` | Semilla inicial de usuarios para pruebas locales |
| `resources/application.properties` | Configuración de H2, puerto del servidor y Hibernate |

### Frontend (`frontend/src/app/`)

| Archivo / Carpeta | Responsabilidad |
|---|---|
| `core/auth.service.ts` | Gestión de sesión (login, logout, almacenamiento local) |
| `core/auth.guard.ts` | Protección de rutas privadas (`canActivate`) |
| `core/api.ts` | Configuración de endpoints y contratos de tipos |
| `core/errores.ts` | Transformación de errores HTTP a mensajes amigables |
| `shared/escudo/` | Componente reutilizable del escudo institucional |
| `pages/login/` | Pantalla y formulario reactivo de inicio de sesión |
| `pages/inicio/` | Pantalla posterior al login según el rol del usuario |
| `app.routes.ts` | Configuración de rutas públicas y privadas |

---

## 6. Solución a Problemas Comunes

| Síntoma | Posible causa | Solución |
|---|---|---|
| *"No hay conexión con el servidor"* | El backend no está iniciado | Iniciar el backend en el puerto 8080 (ver Paso 1) |
| Error de **CORS** en el navegador | Angular está corriendo en un puerto diferente a 4200 | Ajustar el origen permitido en `CorsConfig.java` |
| `Port 8080 was already in use` | Otro proceso tiene tomado el puerto 8080 | Detener el proceso previo o cambiar `server.port` en `application.properties` (y `API_URL` en `frontend/.../api.ts`) |
| Permiso denegado al ejecutar `./mvnw` | Falta permiso de ejecución en Linux/macOS | Ejecutar `chmod +x backend/mvnw` |
| `release version 21 not supported` | Se está usando una versión de Java inferior a 21 | Instalar y configurar JDK 21 (Temurin) |
| `npm install` falla | Versión de Node desactualizada | Usar Node.js versión LTS (20 o 22) |

---

## 7. Flujo de Trabajo con Git

### Clonar y preparar el entorno
```bash
git clone <URL-del-repositorio>
cd <carpeta-del-repositorio>

# Configurar permisos del wrapper
chmod +x backend/mvnw

# Instalar dependencias del frontend
cd frontend
npm install
```

### Comandos frecuentes
```bash
git pull                   # Descargar últimos cambios
git status                 # Ver archivos modificados y sin seguimiento
git add .                  # Preparar cambios para commit
git commit -m "mensaje"    # Registrar cambios con mensaje descriptivo
git push                   # Subir cambios al repositorio
```
