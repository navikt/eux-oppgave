package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.common.uuid1
import no.nav.eux.oppgave.webapp.model.EuxOppgavePrioritetEnum.LAV
import no.nav.eux.oppgave.webapp.model.TestModelOppgaverOpprettelse
import java.time.LocalDate

val oppgaverOpprettelse = TestModelOppgaverOpprettelse(
    oppgaveUuid = null,
    aktoerId = "2850955164683",
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
    opprettetBruker = "test",
    metadata = mapOf(Pair("RINA_SAKID", "123")),
    lagNestenLikOppgave = null,
)

val oppgaverOpprettelseIkkeLagNestenLik = TestModelOppgaverOpprettelse(
    oppgaveUuid = null,
    aktoerId = "2850955164683",
    aktivDato = LocalDate.parse("2024-12-01"),
    prioritet = LAV,
    tema = "GEN",
    oppgavetype = "JFR",
    behandlingstema = "test",
    behandlingstype = "test",
    journalpostId = "453857123",
    saksreferanse = "test",
    tildeltEnhetsnr = "test",
    beskrivelse = "desc",
    fristFerdigstillelse = LocalDate.parse("2024-12-01"),
    opprettetAvEnhetsnr = "test",
    behandlesAvApplikasjon = "test",
    opprettetBruker = "test",
    metadata = mapOf(Pair("RINA_SAKID", "123")),
    lagNestenLikOppgave = false,
)

val oppgaverOpprettelseFeilmelding = TestModelOppgaverOpprettelse(
    oppgaveUuid = null,
    aktoerId = "2850955164683",
    aktivDato = LocalDate.parse("2024-12-01"),
    prioritet = LAV,
    tema = "GEN",
    oppgavetype = "JFR",
    behandlingstema = "test",
    behandlingstype = "test",
    journalpostId = "453857122",
    saksreferanse = "test",
    tildeltEnhetsnr = "test",
    beskrivelse = "feilmelding",
    fristFerdigstillelse = LocalDate.parse("2024-12-01"),
    opprettetAvEnhetsnr = "test",
    behandlesAvApplikasjon = "test",
    opprettetBruker = "test",
    metadata = mapOf(Pair("RINA_SAKID", "123")),
    lagNestenLikOppgave = true,
)

val oppgaverOpprettelseMedUuid = TestModelOppgaverOpprettelse(
    oppgaveUuid = uuid1,
    aktoerId = "2850955164683",
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
    opprettetBruker = "test",
    metadata = mapOf(Pair("RINA_SAKID", "123")),
    lagNestenLikOppgave = true,
)