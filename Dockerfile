# Usa Java 21 en lugar de Java 17
FROM eclipse-temurin:21-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de Spring Boot al contenedor
COPY target/PlayApp-0.0.1-SNAPSHOT.jar /app/PlayApp-0.0.1-SNAPSHOT.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
CMD ["java", "-jar", "/app/PlayApp-0.0.1-SNAPSHOT.jar"]