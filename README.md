# Playapp
[![Spring Boot](https://img.shields.io/badge/Springboot-6AAD3D)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-FF4C41)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-3E6E93)](https://www.mysql.com/downloads/)
[![Docker](https://img.shields.io/badge/Docker-0F4AC3)](https://www.docker.com/)
## ¿Que es Playapp?
Es una aplicacion web  que busca brindar acceso a la compra de productos y servicios en las playas de cartagena con precios justos con el fin de mitigar el impacto de las estafas.
## ¿Que necesito para ejecutarlo?

- IDE Configurado con Spring Boot
- Java JDK version 21
- MySQL
- Redis
- Docker

## ¿Como lo ejecuto?

1. Descargar codigo fuente y abrir proyecto en el IDE
2. Configurar e iniciar servicio de Redis
    1. Modificar los parametros de conexión a redis en el archivo "application.properties" con la información de su servidor Redis.
    2. Iniciar servicio de Redis, esto se puede hacer desde docker(mirar siguiente sección).
3. Compilar y ejecutar el proyecto con la ayuda de su IDE de preferencia

### si no tengo Redis ¿como lo instalo?
La forma mas sencilla de usar redis es descargando la imagen publicada en dockerhub con el siguiente comando:

```bash
docker run -d --name redis -p 6379:6379 redis:7.4
```
* Tenga encuenta que para descargar la imagen tiene que iniciar sesión con su perfil de dockerhub, esto lo puede hacer con el siguiente comando (reemplaze "user", con su usuario de docker):

```bash
docker login -u <user>
```  
