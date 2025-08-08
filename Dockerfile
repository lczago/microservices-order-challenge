ARG MODULE

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

ARG MODULE

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :${MODULE}:bootJar --no-daemon

FROM gcr.io/distroless/java21-debian12
WORKDIR /app

ARG MODULE

COPY --from=build /app/${MODULE}/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
