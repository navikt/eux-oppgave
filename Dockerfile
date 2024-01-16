FROM ghcr.io/navikt/baseimages/temurin:21

ADD eux-oppgave-webapp/target/eux-oppgave.jar /app/app.jar
