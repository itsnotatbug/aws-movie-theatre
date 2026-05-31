FROM maven:3.9-eclipse-temurin-21 AS build
ARG APP_VERSION=1.0.0
ARG GIT_REVISION=unknown
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests -Drevision=${APP_VERSION} -Dgit.revision=${GIT_REVISION}

FROM tomcat:9.0-jdk21
ARG APP_VERSION=1.0.0
ARG GIT_REVISION=unknown
LABEL org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.revision="${GIT_REVISION}" \
      org.opencontainers.image.source="https://github.com/itsnotatbug/aws-movie-theatre" \
      org.opencontainers.image.title="aws-movie-theatre"
RUN groupadd -r app \
    && useradd -r -g app app \
    && chown -R app:app /usr/local/tomcat
WORKDIR /app
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
USER app
EXPOSE 8080
CMD ["catalina.sh", "run"]
