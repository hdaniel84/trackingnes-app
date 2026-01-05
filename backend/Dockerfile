# Ya no necesitamos Maven ni compilar aquí.
# Usamos directamente la imagen de Java para ejecutar.
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Crear grupo y usuario de seguridad (Best Practice)
RUN addgroup -S spring && adduser -S spring -G spring

# AQUÍ ESTÁ EL CAMBIO CLAVE:
# Copiamos el .jar que tú subiste manualmente a la carpeta del servidor.
# El asterisco *.jar toma cualquier archivo .jar que esté ahí.
COPY *.jar app.jar

# Cambiamos permisos para que el usuario 'spring' sea el dueño
RUN chown spring:spring app.jar

# Cambiamos al usuario limitado
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]