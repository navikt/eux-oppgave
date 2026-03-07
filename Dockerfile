FROM gcr.io/distroless/java25
#FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
COPY eux-oppgave-webapp/target/eux-oppgave.jar /app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "-jar", "/app.jar"]
