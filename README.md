# Playapp
[![Spring Boot](https://img.shields.io/badge/Springboot-6AAD3D)](https://redis.io/)
[![Redis](https://img.shields.io/badge/Redis-FF4C41)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-3E6E93)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-0F4AC3)](https://redis.io/)
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
2. Configurar e iniciar servicio de base de Datos MySQL
    1. Editar los parametros de conexión en el archivo "application.properties" con la información de su servidor MySQL .
    2. Iniciar servicio de MySQL
    3. Crear una nueva base datos que se llame "playappdb" (En caso de no haber modificado el parametro "Server")
3. Configurar e iniciar servicio de Redis
    1. Modificar los parametros de conexión a redis en el archivo "application.properties" con la información de su servidor Redis.
    2. Iniciar servicio de Redis, esto se puede hacer desde docker(mirar siguiente sección).
4. Compilar y ejecutar el proyecto con la ayuda de su IDE de preferencia

### si no tengo Redis ¿como lo instalo?
La forma mas sencilla usar redis es descargando la imagen publicada en dockerhub con el siguiente comando:

```bash
docker run -d --name redis -p 6379:6379 redis:7.4
```
* Tenga encuenta que para descargar la imagen tiene que estar logeado con su perfil de dockerhub, esto lo puede hacer con el siguiente comando (reemplaze "user", con su usuario de docker):

```bash
docker login -u <user>
```  
