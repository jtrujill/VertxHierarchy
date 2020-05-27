FROM gradle:6.4.1-jdk14 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:14-slim-buster

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/
WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-jar", "./VertxHierarchy-1.0-SNAPSHOT-all.jar"]

