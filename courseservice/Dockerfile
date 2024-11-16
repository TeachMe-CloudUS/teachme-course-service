# 1. Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# 2. Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copia el archivo JAR generado al contenedor
COPY target/*.jar app.jar

# 4. Exponer el puerto en el que corre tu aplicación
EXPOSE 8081

# 5. Comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]