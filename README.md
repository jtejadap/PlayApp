# PlayApp

Proyecto Spring Boot 3 + Java 21 con MongoDB y Redis.

## Objetivo de esta sincronizacion local

Esta configuracion esta pensada para que **todo quede aislado por proyecto**:

- Docker con nombre de proyecto propio: `playapp-local`
- Volumenes Docker propios: `playapp_mongo_data`, `playapp_redis_data`
- Variables de entorno solo en `config/application-secrets.env`
- VS Code configurado en `.vscode/` para este workspace
- Sin cambios en configuraciones globales de Git, Docker Desktop o VS Code

## Requisitos

- Java 21 (opcional si usas el instalador local `scripts/setup-java21.ps1`)
- Docker Desktop + Docker Compose
- Git (opcional para conectar a GitHub)

## Java 21 local (sin tocar tu Java global)

Instala JDK 21 portable dentro de este repo:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\setup-java21.ps1
```

Se instala en `.tools/jdk-21` y se usa solo para PlayApp.

## 1) Configurar variables locales

Archivo base:

`config/application-secrets.env.example`

Copia a:

`config/application-secrets.env`

Variables importantes:

- `MONGODB_URI`
- `MONGODB_DATABASE`
- `REDIS_HOST`
- `REDIS_PORT`
- `MP_ACCESS_TOKEN`
- `PLAYAPP_BASE_URL`
- `PLAYAPP_ADMIN_EMAIL`
- `PLAYAPP_ADMIN_PASSWORD`

Puertos recomendados para no chocar con otros proyectos:

- Mongo local del proyecto: `27018`
- Redis local del proyecto: `6380`
- App local: `8080`

## 2) Levantar Mongo y Redis solo para PlayApp

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
```

Para detener:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-down.ps1
```

## 3) Ejecutar la app local

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-playapp.ps1
```

El script:

- carga `config/application-secrets.env`
- levanta `mongo` y `redis` del `docker-compose.yml` (si Docker esta disponible)
- si detecta Java 21, inicia Spring Boot local
- si NO detecta Java 21 (por ejemplo Java 17), cambia automaticamente a modo Docker (mongo + redis + app)

## 4) Ejecutar todo en Docker (incluyendo app)

```powershell
docker-compose --profile app up -d --build
```

## 5) VS Code (solo workspace)

Se incluyeron archivos en `.vscode/`:

- `settings.json`
- `tasks.json`
- `launch.json`
- `extensions.json`

No afectan otros proyectos.

## 6) GitHub (sin tocar global)

Si quieres conectar este repo local a GitHub:

```powershell
git remote add origin https://github.com/jtejadap/PlayApp.git
git branch -M main
```

Luego revisa antes de push:

```powershell
git status
```

## Seguridad

Se removieron secretos hardcodeados del codigo:

- credenciales de correo en `application.properties`
- token de MercadoPago en `PaymentService`
- URL fija de ngrok para callbacks de pago

Ahora todo viene por variables locales del proyecto.
