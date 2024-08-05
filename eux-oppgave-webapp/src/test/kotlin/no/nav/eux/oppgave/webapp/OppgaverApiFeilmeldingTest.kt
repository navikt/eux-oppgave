package no.nav.eux.oppgave.webapp

import io.kotest.assertions.json.shouldMatchJsonResource
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelseFeilmelding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity

class OppgaverApiFeilmeldingTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - feilmelding - 400`() {
        val createResponse = restTemplate
            .postForEntity<String>(
                oppgaverUrl,
                oppgaverOpprettelseFeilmelding.httpEntity
            )
        val response = createResponse.body!!
        response shouldMatchJsonResource "/dataset/expected/oppgave-feilmelding.json"
        assertThat(createResponse.statusCode.value()).isEqualTo(400)
    }
}
