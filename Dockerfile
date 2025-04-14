FROM gradle:8.10.1-jdk21 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble

FROM eclipse-temurin:21.0.4_7-jre
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "/app/spring-boot-application.jar"]