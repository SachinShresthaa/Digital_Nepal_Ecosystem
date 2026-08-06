# Digital Nepal Ecosystem - Backend Dockerfile
# Multi-stage build for Java 21 + Spring Boot 3.x
# Build stage optimized for CI/CD pipeline

# ============================================================================
# STAGE 1: BUILD
# ============================================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy project files
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml .
COPY modules/ modules/

# NOTE: `stub-backend` is the canonical runnable Spring Boot module in this multi-module project.
# The CI pipeline and Docker runtime image expect the artifact to be produced at
# `modules/stub-backend/target/*.jar` and will use that JAR as the application entrypoint.

# Ensure wrapper is executable and run a full multi-module build
RUN chmod +x mvnw || true
# Install curl/unzip so the mvnw script can download the maven-wrapper.jar and Maven distro
RUN apt-get update && apt-get install -y --no-install-recommends curl unzip ca-certificates && rm -rf /var/lib/apt/lists/*

# Use the wrapper to build the project
RUN ./mvnw -B  -DskipTests clean package -pl modules/stub-backend -am

# Target the explicitly named production binary—no wildcards, no guessing!
RUN mkdir -p /build-output && \
    cp modules/stub-backend/target/production-app.jar /build-output/app.jar

# ============================================================================
# STAGE 2: RUNTIME
# ============================================================================
FROM eclipse-temurin:21-jre

LABEL maintainer="Digital Nepal DevOps <devops@digital-nepal.gov.np>"
LABEL description="Digital Nepal Citizen Ecosystem Backend Service"
LABEL version="1.0"

WORKDIR /opt/digital-nepal

# Install curl for health checks
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Copy compiled JAR from builder
COPY --from=builder /build-output/app.jar /opt/digital-nepal/app.jar

# Create non-root user for security
RUN useradd -r -u 1500 -g 1000 --no-create-home appuser && \
    chown -R 1500:1000 /opt/digital-nepal

USER appuser

CMD ["java", "-jar", "/opt/digital-nepal/app.jar"]
