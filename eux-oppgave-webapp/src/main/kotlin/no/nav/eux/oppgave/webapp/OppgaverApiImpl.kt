package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.openapi.api.OppgaverApi
import no.nav.eux.oppgave.openapi.model.*
import no.nav.eux.oppgave.service.*
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OppgaverApiImpl(
    val oppgaveService: OppgaveService,
    val ferdigstillService: FerdigstillService,
    val tokenContextService: TokenContextService,
    val tildelEnhetsnummerService: TildelEnhetsnummerService
) : OppgaverApi {

    @Protected
    override fun opprettOppgave(
        oppgaveCreateOpenApiType: OppgaveCreateOpenApiType
    ): ResponseEntity<OppgaveOpenApiType> =
        oppgaveService
            .mdc(journalpostId = oppgaveCreateOpenApiType.journalpostId)
            .opprettOppgave(oppgaveCreateOpenApiType.euxOppgaveOpprettelse(tokenContextService.navIdent))
            .oppgaveOpenApiType
            .toCreatedResponseEntity()

    @Protected
    override fun ferdigstillOppgaver(
        ferdigstillOpenApiType: FerdigstillOpenApiType
    ): ResponseEntity<FerdigstillResponsOpenApiType> =
        ferdigstillService
            .ferdigstillOppgaver(
                journalpostIder = ferdigstillOpenApiType.journalpostIder,
                personident = ferdigstillOpenApiType.personident
            )
            .ferdigstillResponsOpenApiType
            .toOkResponseEntity()

    @Protected
    override fun tildelEnhetsnummer(
        tildelEnhetsnrOpenApiType: TildelEnhetsnrOpenApiType
    ): ResponseEntity<Unit> =
        tildelEnhetsnummerService
            .mdc(journalpostId = tildelEnhetsnrOpenApiType.journalpostId)
            .tildelEnhetsnummer(
                journalpostId = tildelEnhetsnrOpenApiType.journalpostId,
                tildeltEnhetsnr = tildelEnhetsnrOpenApiType.tildeltEnhetsnr,
                kommentar = tildelEnhetsnrOpenApiType.kommentar ?: ""
            )
            .toEmptyResponseEntity()

    @Protected
    override fun behandleSedFraJournalpostId(
        behandleSedFraJournalpostIdOpenApiType: BehandleSedFraJournalpostIdOpenApiType
    ) =
        oppgaveService
            .mdc(journalpostId = behandleSedFraJournalpostIdOpenApiType.journalpostId)
            .behandleSedFraJournalpostId(behandleSedFraJournalpostIdOpenApiType.journalpostId)
            .oppgaveOpenApiType
            .toCreatedResponseEntity()
}
