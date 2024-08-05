package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.model.TestModelFinnOppgaver
import java.time.LocalDate

val finnOppgaverDatasetBehandlingstema = TestModelFinnOppgaver(
    fristFom = LocalDate.now(),
    fristTom = LocalDate.now(),
    tema = "BAR",
    oppgavetype = "FREM",
    behandlingstema = "ab0058",
    null,
    limit = 200,
    offset = 10
)
