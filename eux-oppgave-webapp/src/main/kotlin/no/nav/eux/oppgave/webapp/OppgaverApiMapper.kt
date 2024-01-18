package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.model.common.toEnum
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.openapi.model.FerdigstillResponsOpenApiType
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

fun OppgaveCreateOpenApiType.euxOppgaveOpprettelse(navIdent: String) =
    EuxOppgaveOpprettelse(
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
        tema = tema,
        opprettetBruker = navIdent
    )

val List<EuxOppgave>.ferdigstillResponsOpenApiType
    get() = FerdigstillResponsOpenApiType(oppgaver = map { it.oppgaveOpenApiType })
