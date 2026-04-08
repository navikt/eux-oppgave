package no.nav.eux.oppgave.webapp

import io.kotest.assertions.json.shouldMatchJsonResource
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.common.token
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelseFeilmelding
import org.junit.jupiter.api.Test

class OppgaverApiFeilmeldingTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - feilmelding - 400`() {
        val createResponse = restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseFeilmelding)
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(String::class.java)
            .returnResult()
        createResponse.responseBody!! shouldMatchJsonResource "/dataset/expected/oppgave-feilmelding.json"
    }
}
