FROM eclipse-temurin:25-jdk AS build

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN groupadd --system uinsight && useradd --system --gid uinsight uinsight

COPY --from=build /build/target/*.jar app.jar

RUN chown uinsight:uinsight app.jar
USER uinsight

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
