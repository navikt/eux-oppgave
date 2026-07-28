package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.openapi.model.Prioritet
import java.time.LocalDate

val oppdaterOppgaveDataset = OppgaveOpenApiType(
    aktoerId = "2850955164683",
    aktivDato = LocalDate.parse("2024-12-01"),
    prioritet = Prioritet.LAV,
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
    id = 190402,
    versjon = 1
)

val oppdaterOppgaveFeiletDataset = OppgaveOpenApiType(
    aktoerId = "2850955164683",
    aktivDato = LocalDate.parse("2024-12-01"),
    prioritet = Prioritet.LAV,
    tema = "GEN",
    oppgavetype = "JFR",
    behandlingstema = "test",
    behandlingstype = "test",
    journalpostId = "222222",
    saksreferanse = "test",
    tildeltEnhetsnr = "test",
    beskrivelse = "desc",
    fristFerdigstillelse = LocalDate.parse("2024-12-01"),
    opprettetAvEnhetsnr = "test",
    behandlesAvApplikasjon = "test",
    id = 999999,
    versjon = 1
)
