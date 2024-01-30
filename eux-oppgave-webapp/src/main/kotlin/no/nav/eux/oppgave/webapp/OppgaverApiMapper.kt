package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.model.common.toEnum
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.dto.OppgaveFerdigstilling
import no.nav.eux.oppgave.openapi.model.*

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
        tema = tema,
        opprettetBruker = navIdent
    )

val List<OppgaveFerdigstilling>.ferdigstillResponsOpenApiType
    get() = FerdigstillResponsOpenApiType(oppgaver = map { it.oppgaveFerdigstillingOpenApiType })

val OppgaveFerdigstilling.oppgaveFerdigstillingOpenApiType
    get() = OppgaveFerdigstillingOpenApiType(
        status = status.name.toEnum(),
        oppgave = euxOppgave?.oppgaveOpenApiType,
        beskrivelse = beskrivelse
    )
