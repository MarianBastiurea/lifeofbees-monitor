# =========================
# Build stage
# =========================
FROM maven:3.9-eclipse-temurin-23 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:23-jdk

WORKDIR /app

# Docker CLI is required by:
# ApplicationStatusTool
# ReadApplicationLogTool
# RestartApplicationTool
RUN apt-get update \
    && apt-get install -y --no-install-recommends docker.io \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder \
    /app/target/lifeofbees-monitor-0.0.1-SNAPSHOT.jar \
    app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]