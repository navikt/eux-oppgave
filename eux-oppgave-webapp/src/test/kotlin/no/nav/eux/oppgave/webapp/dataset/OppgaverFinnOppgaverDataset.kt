package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.model.TestModelFinnOppgaver
import java.time.LocalDate

val finnOppgaverFristFom: LocalDate = LocalDate.parse("2024-12-01")
val finnOppgaverFristTom: LocalDate = LocalDate.parse("2024-12-31")

val finnOppgaverDatasetBehandlingstema = TestModelFinnOppgaver(
    fristFom = finnOppgaverFristFom,
    fristTom = finnOppgaverFristTom,
    tema = "BAR",
    oppgavetype = "FREM",
    behandlingstema = "ab0058",
    behandlingstype = null,
    limit = 200,
    offset = 10
)

val finnOppgaverDatasetBehandlingstype = TestModelFinnOppgaver(
    fristFom = finnOppgaverFristFom,
    fristTom = finnOppgaverFristTom,
    tema = "BAR",
    oppgavetype = "FREM",
    behandlingstema = null,
    behandlingstype = "ae0106",
    limit = 200,
    offset = 10
)

val finnOppgaverDatasetUtenPaginering = TestModelFinnOppgaver(
    fristFom = finnOppgaverFristFom,
    fristTom = finnOppgaverFristTom,
    tema = "BAR",
    oppgavetype = "FREM",
    behandlingstema = "ab0058",
    behandlingstype = null,
    limit = null,
    offset = null
)
