package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.openapi.api.OppgaverApi
import no.nav.eux.oppgave.openapi.model.OppgaveCreateOpenApiType
import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.service.OppgaveService
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OppgaverApiImpl(
    val service: OppgaveService
) : OppgaverApi {

    @Protected
    override fun opprettOppgave(
        oppgaveCreateOpenApiType: OppgaveCreateOpenApiType
    ): ResponseEntity<OppgaveOpenApiType> =
        service
            .opprettOppgave(oppgaveCreateOpenApiType.euxOppgaveOpprettelse)
            .oppgaveOpenApiType
            .toCreatedResponseEntity()
}
