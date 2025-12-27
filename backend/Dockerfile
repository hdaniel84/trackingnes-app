# Etapa 1: Compilación (Maven)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compilamos saltando tests para ir rápido
RUN mvn clean package -DskipTests
#RUN mvn clean package -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

# Etapa 2: Ejecución (Ligera)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copiamos solo el jar generado
COPY --from=build /app/target/*.jar app.jar

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
