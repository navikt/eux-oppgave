package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.model.EuxOppgavePrioritetEnum.LAV
import no.nav.eux.oppgave.webapp.model.TestModelFinnOppgaver
import no.nav.eux.oppgave.webapp.model.TestModelFinnOppgaverRespons
import no.nav.eux.oppgave.webapp.model.TestModelOppgaverOpprettelse
import no.nav.eux.oppgave.webapp.model.TestModelTildelEnhetsnr
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
val finnOppgaverDatasetBehandlingstype = TestModelFinnOppgaver(
    fristFom = LocalDate.now(),
    fristTom = LocalDate.now(),
    tema = "BAR",
    oppgavetype = "FREM",
    null,
    behandlingstype = "ae0106",
    null,
    null
)
