FROM gcr.io/distroless/java21
COPY eux-oppgave-webapp/target/eux-oppgave.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
