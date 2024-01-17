package no.nav.eux.oppgave.service

import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.model.EuxOppgave
import no.nav.eux.oppgave.model.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.EuxOppgaveStatus
import no.nav.eux.oppgave.model.toEnum

val EuxOppgaveOpprettelse.oppgaveOpprettelse
    get() =
        OppgaveOpprettelse(
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
            euxOppgaveStatus = status.name.toEnum<EuxOppgaveStatus>(),
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt,
            tema = tema,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
        )
