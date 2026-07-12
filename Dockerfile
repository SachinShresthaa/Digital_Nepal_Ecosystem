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
#RUN ./mvnw -B -T1C -DskipTests clean package
#  UPDATED FIXED LINE
RUN ./mvnw -B -T1C -DskipTests clean package -pl modules/stub-backend -am



# After build, we expect at least one runnable Spring Boot module, prefer stub-backend
# RUN mkdir -p /build-output && \
#     if ls modules/stub-backend/target/*.jar 1> /dev/null 2>&1; then \
#       cp modules/stub-backend/target/*.jar /build-output/app.jar ; \
#     else \
#       echo "ERROR: Expected artifact modules/stub-backend/target/*.jar not found" >&2 ; exit 1 ; \
#     fi
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
# RUN apk add --no-cache curl

#using apt-get update
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Copy compiled JAR from builder
COPY --from=builder /build-output/app.jar /opt/digital-nepal/app.jar

# Create non-root user for security
# RUN addgroup -g 1000 appuser && \
#     adduser -D -u 1000 -G appuser appuser && \
#     chown -R appuser:appuser /opt/digital-nepal

# using latest debian/ubuntu syntax
# RUN groupadd -g 1000 appuser && \
#     useradd -r -u 1000 -g appuser --no-create-home appuser && \
#     chown -R appuser:appuser /opt/digital-nepal


# Reuse the existing system group and assign permissions safely
RUN useradd -r -u 1500 -g 1000 --no-create-home appuser && \
    chown -R 1500:1000 /opt/digital-nepal




USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "/opt/digital-nepal/app.jar"]