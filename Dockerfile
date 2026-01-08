FROM gcr.io/distroless/java25
COPY eux-oppgave-webapp/target/eux-oppgave.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
