FROM maven AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build /app/target/riderapp-1.0.jar /riderapp-1.0.jar
ENTRYPOINT ["java", "-jar", "/riderapp-1.0.jar"]