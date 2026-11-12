# Directorio de funcionarios

Spring Boot 3 (Java 21, MVC) + MySQL 8. Frontend en HTML, CSS y JavaScript vanilla servido desde `static/`.

## Ejecutar
1. Base MySQL en Aiven (defaultdb). Las tablas se crean solas al iniciar.
2. Abre `src/main/resources/application.properties` y pega tu contrasena de Aiven donde dice `PEGA_AQUI_TU_CLAVE` (o usa la variable de entorno `DB_PASSWORD`).
3. `mvn spring-boot:run` y abrir http://localhost:8080

## API (`/api/v1/funcionarios`)
| Operación | Método y ruta | Respuesta |
|---|---|---|
| Listar | GET / | 200 / 204 |
| Consultar | GET /{id} | 200 / 404 |
| Registrar | POST / | 201 / 400 |
| Actualizar | PUT /{id} | 200 / 400 / 404 |
| Eliminar | DELETE /{id} | 204 / 404 |
| Buscar por nombre | GET /?nombre=ana | 200 / 204 |
| Filtrar por departamento | GET /?departamento=Contabilidad | 200 / 204 |

## Seguridad (Spring Security + HTTP Basic)
- `config/SecurityConfig.java` protege toda la API `/api/**` con **HTTP Basic**; los archivos estáticos (`login.html`, `css`, `js`) son públicos.
- Al abrir la app sin sesión se redirige a `login.html`. El formulario valida usuario y contraseña contra `GET /api/v1/auth/me` y guarda la credencial en `sessionStorage` (se borra al cerrar la pestaña o con **Cerrar sesión**).
- Usuario por defecto: `admin` / `admin123`. Cámbialo con las variables de entorno `APP_USER` y `APP_PASSWORD`.
- Sin credenciales, la API responde `401`:
  `curl -u admin:admin123 http://localhost:8080/api/v1/funcionarios`
