# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY ums/pom.xml ./
RUN mvn -q -B dependency:go-offline

COPY ums/src ./src
RUN mvn -q -B -DskipTests package \
    && jar="$(ls target/*.jar | grep -v '\.original$' | head -n1)" \
    && cp "$jar" /build/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /build/app.jar /app/app.jar

EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
