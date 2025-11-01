# Dockerfile
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY pom.xml ./
COPY src src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# set default Spring Kafka bootstrap servers (can be overridden at runtime)
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# copy app jar from builder (adjust name/version if needed)
COPY --from=builder /workspace/target/trades-capture-service-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
