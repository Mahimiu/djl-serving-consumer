# syntax=docker/dockerfile:1.6

# ============================================================
# Stage 1: Build the application (JDK + Maven)
# ============================================================
FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /build

# Maven-Wrapper zuerst kopieren (Layer-Cache: pom.xml ändert sich selten)
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Windows-Zeilenenden in mvnw fixen + ausführbar machen
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Dependencies vorab laden (gecached solange pom.xml gleich bleibt)
RUN ./mvnw dependency:go-offline -B

# Source code und Build
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime (nur JRE - schlanker und sicherer)
# ============================================================
FROM eclipse-temurin:25-jre-noble

LABEL org.opencontainers.image.title="DJL Serving Consumer"
LABEL org.opencontainers.image.description="Spring Boot WebFlux client for DJL Serving (Sentiment Analysis)"
LABEL org.opencontainers.image.source="https://github.com/Mahimiu/djl-serving-consumer"

WORKDIR /app

# Non-Root User (Security)
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

# Nur fertige JAR aus Builder - keine Build-Tools, kein Source
COPY --from=builder --chown=spring:spring /build/target/consumer-0.0.1-SNAPSHOT.jar app.jar

# Container-aware JVM Settings
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]