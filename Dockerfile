FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

FROM tomcat:9.0-jdk21
RUN groupadd -r app \
    && useradd -r -g app app \
    && chown -R app:app /usr/local/tomcat
WORKDIR /app
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
USER app
EXPOSE 8080
CMD ["catalina.sh", "run"]