## build maven stage 
FROM maven:3.8.3-openjdk-17-slim as build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests=true

## run stage
FROM amazoncorretto:17-alpine-jdk as run
WORKDIR /run
COPY --from=build /app/target/codeSphere-0.0.1-SNAPSHOT.jar /run/codeSphere-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/run/codeSphere-0.0.1-SNAPSHOT.jar"]
