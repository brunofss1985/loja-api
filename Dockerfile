# Estágio 1: Build com Maven
# Usa uma imagem com Maven e o JDK 17 para compilar o projeto.
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copia o pom.xml e os arquivos de configuração do Maven para aproveitar o cache do Docker.
COPY pom.xml .
COPY .mvn .mvn
# Baixa as dependências do Maven.
RUN mvnw dependency:go-offline
# Copia o resto do código fonte.
COPY src ./src
# Executa o comando de build para criar o JAR executável.
RUN ./mvnw clean package -DskipTests

---

# Estágio 2: Runtime com JRE
# Usa uma imagem mais leve com apenas o JRE 17 para rodar a aplicação.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia o JAR do estágio de build.
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que sua aplicação utiliza.
EXPOSE 8080
# Define o comando de entrada que executa o JAR com o perfil de produção.
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]