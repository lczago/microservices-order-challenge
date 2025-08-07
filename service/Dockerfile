FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .
COPY ../domain ./domain

RUN chmod +x ./gradlew

RUN ./gradlew :service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]