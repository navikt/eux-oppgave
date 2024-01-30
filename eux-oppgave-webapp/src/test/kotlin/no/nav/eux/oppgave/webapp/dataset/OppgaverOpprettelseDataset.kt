package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.model.EuxOppgavePrioritetEnum.LAV
import no.nav.eux.oppgave.webapp.model.TestModelOppgaverOpprettelse
import java.time.LocalDate

val oppgaverOpprettelse = TestModelOppgaverOpprettelse(
    aktivDato = LocalDate.parse("2024-12-01"),
    prioritet = LAV,
    tema = "GEN",
    oppgavetype = "JFR",
    behandlingstema = "test",
    behandlingstype = "test",
    journalpostId = "453857122",
    saksreferanse = "test",
    tildeltEnhetsnr = "test",
    beskrivelse = "desc",
    fristFerdigstillelse = LocalDate.parse("2024-12-01"),
    opprettetAvEnhetsnr = "test",
    behandlesAvApplikasjon = "test",
    opprettetBruker = "test"
)
