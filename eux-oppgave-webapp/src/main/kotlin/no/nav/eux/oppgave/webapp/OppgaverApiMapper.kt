package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.model.EuxOppgave
import no.nav.eux.oppgave.model.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.toEnum
import no.nav.eux.oppgave.openapi.model.OppgaveCreateOpenApiType
import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.openapi.model.Status

val EuxOppgave.oppgaveOpenApiType
    get() =
        OppgaveOpenApiType(
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
            status = euxOppgaveStatus?.name?.toEnum<Status>(),
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt,
            tema = tema,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
        )

val OppgaveCreateOpenApiType.euxOppgaveOpprettelse
    get() =
        EuxOppgaveOpprettelse(
            aktoerId = aktoerId,
            aktivDato = aktivDato,
            prioritet = prioritet.name.toEnum(),
            oppgavetype = oppgavetype,
            behandlingstema = behandlingstema,
            behandlingstype = behandlingstype,
            journalpostId = journalpostId,
            saksreferanse = saksreferanse,
            tildeltEnhetsnr = tildeltEnhetsnr,
            beskrivelse = beskrivelse,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetAvEnhetsnr = opprettetAvEnhetsnr,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
            tema = tema
        )
