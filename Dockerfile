# 1️⃣ Imagen base con JDK 17 y Maven ya instalado
FROM maven:3.9.3-eclipse-temurin-17-alpine

# 2️⃣ Carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3️⃣ Copia pom.xml y src
COPY pom.xml .
COPY src ./src

# 4️⃣ Compila el proyecto y genera el JAR (sin tests)
RUN mvn clean package -DskipTests

# 5️⃣ Expone el puerto que Render asignará
ENV PORT=8084
EXPOSE $PORT

# 6️⃣ Comando para iniciar la app
CMD ["sh", "-c", "java -jar target/mikhuy-backend-*.jar"]
