# 1️⃣ Usa un JDK 17 ligero
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3️⃣ Copia pom.xml y descarga dependencias para acelerar build
COPY pom.xml .
# Crea carpeta src temporal para que Maven no falle
RUN mkdir -p src && echo "" > src/placeholder.txt
# Descarga dependencias
RUN ./mvnw dependency:go-offline -B || \
    (apk add --no-cache bash && mvn dependency:go-offline -B)

# 4️⃣ Copia todo el proyecto al contenedor
COPY . .

# 5️⃣ Compila el proyecto y genera el JAR (sin tests)
RUN ./mvnw clean package -DskipTests

# 6️⃣ Expone el puerto que Render asignará
ENV PORT=8084
EXPOSE $PORT

# 7️⃣ Comando para iniciar la app
CMD ["sh", "-c", "java -jar target/mikhuy-backend-*.jar"]