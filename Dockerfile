FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk AS build

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -ntp -DskipTests package


FROM eclipse-temurin:25-jre

LABEL org.opencontainers.image.source=https://github.com/intorwacja/WatchTime

RUN --mount=type=bind,source=emudhra-blocked.certs,target=/tmp/emudhra-blocked.certs \
    cat /tmp/emudhra-blocked.certs >> "$JAVA_HOME/lib/security/blocked.certs"

RUN groupadd --system watchtime && useradd --system --gid watchtime watchtime

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

USER watchtime
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
