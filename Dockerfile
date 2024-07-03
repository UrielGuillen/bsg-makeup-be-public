FROM gradle:8.4.0-jdk17 AS build

COPY --chown=gradle:gradle src /home/gradle/src
COPY --chown=gradle:gradle gradlew /home/gradle
COPY --chown=gradle:gradle build.gradle.kts /home/gradle
COPY --chown=gradle:gradle settings.gradle.kts /home/gradle

WORKDIR /home/gradle
RUN gradle clean build --no-daemon -x test

FROM openjdk:17.0.1-jdk-slim
EXPOSE 8080

WORKDIR /app
COPY --from=build /home/gradle/build/libs/*.jar /app/app.jar

# Reduce the number of layers
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
