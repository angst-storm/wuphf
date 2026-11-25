# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
ARG MODULE=ums
WORKDIR /build

COPY pom.xml ./
COPY common/pom.xml ./common/
COPY ums/pom.xml ./ums/
COPY messages/pom.xml ./messages/

COPY common/src ./common/src
COPY ums/src ./ums/src
COPY messages/src ./messages/src

RUN mvn -q -B -pl ${MODULE} -am -DskipTests package \
    && jar="$(ls ${MODULE}/target/*.jar | grep -v '\.original$' | head -n1)" \
    && cp "$jar" /build/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /build/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
