package no.nav.eux.oppgave.service

import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.model.common.toEnum
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.dto.EuxOppgaveStatusEnum
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus

val EuxOppgaveOpprettelse.oppgaveOpprettelse
    get() =
        OppgaveOpprettelse(
            aktoerId = aktoerId,
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
        tema = tema,
        status = EuxOppgaveStatus.Status.UNDER_OPPRETTELSE,
        beskrivelse = beskrivelse,
        opprettetBruker = opprettetBruker,
        endretBruker = opprettetBruker,
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
