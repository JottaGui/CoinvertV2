# ========= STAGE 1 - BUILD =========
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia pom primeiro (melhora cache)
COPY pom.xml .

# Baixa dependências
RUN mvn dependency:go-offline

# Copia o restante do projeto
COPY src ./src

# Gera o jar ignorando testes
RUN mvn clean package -DskipTests


# ========= STAGE 2 - RUNTIME =========
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado do stage anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]