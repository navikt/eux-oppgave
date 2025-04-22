package no.nav.eux.oppgave.service

import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.integration.model.Status
import no.nav.eux.oppgave.model.common.toEnum
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.dto.EuxOppgaveStatusEnum
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.TILDELER_ENHETSNR
import java.util.UUID.randomUUID

val EuxOppgaveOpprettelse.oppgaveOpprettelse
    get() =
        OppgaveOpprettelse(
            personident = aktoerId,
            beskrivelse = beskrivelse,
            tildeltEnhetsnr = tildeltEnhetsnr,
            journalpostId = journalpostId,
            aktivDato = aktivDato,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetAvEnhetsnr = opprettetAvEnhetsnr,
            prioritet = prioritet.name.toEnum(),
            behandlingstema = behandlingstema,
            saksreferanse = saksreferanse,
            oppgavetype = oppgavetype,
            behandlingstype = behandlingstype,
            tema = tema,
            metadata = metadata
        )

val Oppgave.oppgaveOpprettelse
    get() =
        OppgaveOpprettelse(
            personident = aktoerId,
            beskrivelse = beskrivelse,
            tildeltEnhetsnr = tildeltEnhetsnr,
            journalpostId = journalpostId,
            aktivDato = aktivDato,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetAvEnhetsnr = opprettetAvEnhetsnr,
            prioritet = prioritet.name.toEnum(),
            behandlingstema = behandlingstema,
            saksreferanse = saksreferanse,
            oppgavetype = oppgavetype,
            behandlingstype = behandlingstype,
            tema = tema,
            metadata = emptyMap(),
        )

val Oppgave.euxOppgave
    get() =
        EuxOppgave(
            id = id,
            oppgavetype = oppgavetype,
            tildeltEnhetsnr = tildeltEnhetsnr,
            aktivDato = aktivDato,
            euxOppgavePrioritet = prioritet.name.toEnum(),
            versjon = versjon,
            aktoerId = aktoerId,
            behandlingstema = behandlingstema,
            behandlingstype = behandlingstype,
            journalpostId = journalpostId,
            saksreferanse = saksreferanse,
            beskrivelse = beskrivelse,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetAvEnhetsnr = opprettetAvEnhetsnr,
            euxOppgaveStatus = status.name.toEnum<EuxOppgaveStatusEnum>(),
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt,
            tema = tema,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
        )

val EuxOppgaveOpprettelse.euxOppgaveStatus
    get() = EuxOppgaveStatus(
        oppgaveUuid = oppgaveUuid ?: randomUUID(),
        tema = tema,
        status = EuxOppgaveStatus.Status.UNDER_OPPRETTELSE,
        beskrivelse = beskrivelse,
        opprettetBruker = opprettetBruker,
        endretBruker = opprettetBruker,
    )

val Oppgave.euxOppgaveStatus
    get() = EuxOppgaveStatus(
        tema = tema,
        status = EuxOppgaveStatus.Status.UNDER_OPPRETTELSE,
        beskrivelse = beskrivelse,
        opprettetBruker = "ukjent",
        endretBruker = "ukjent",
    )

fun Oppgave.toEuxOppgaveStatusUnderFerdigstilling(navIdent: String) =
    EuxOppgaveStatus(
        oppgaveId = id,
        tema = tema,
        status = EuxOppgaveStatus.Status.UNDER_FERDIGSTILLING,
        beskrivelse = beskrivelse,
        opprettetBruker = "under-ferdigstilling",
        endretBruker = navIdent
    )

fun Oppgave.toEuxOppgaveTildelerEnhetsnummer(navIdent: String) =
    EuxOppgaveStatus(
        oppgaveId = id,
        tema = tema,
        status = TILDELER_ENHETSNR,
        beskrivelse = beskrivelse,
        opprettetBruker = "tildeler-enhetsnummer",
        endretBruker = navIdent
    )

val EuxOppgave.oppgave
    get() =
        Oppgave(
            id = id,
            oppgavetype = oppgavetype,
            tildeltEnhetsnr = tildeltEnhetsnr,
            aktivDato = aktivDato,
            prioritet = euxOppgavePrioritet.name.toEnum(),
            versjon = versjon,
            aktoerId = aktoerId,
            behandlingstema = behandlingstema,
            behandlingstype = behandlingstype,
            journalpostId = journalpostId,
            saksreferanse = saksreferanse,
            beskrivelse = beskrivelse,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetAvEnhetsnr = opprettetAvEnhetsnr,
            status = euxOppgaveStatus?.name?.toEnum<Status>() ?: Status.AAPNET,
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt,
            tema = tema,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
        )
