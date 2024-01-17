package no.nav.eux.oppgave.integration.client

import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Component
class OppgaveClient(
    @Value("\${endpoint.oppgave}")
    val oppgaveUrl: String,
    val oppgaveRestTemplate: RestTemplate
) {

    fun opprettOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave =
        RestClient.create(oppgaveRestTemplate)
            .post()
            .uri("${oppgaveUrl}/api/v1/oppgaver")
            .contentType(APPLICATION_JSON)
            .body(oppgaveOpprettelse)
            .retrieve()
            .body()!!
}
