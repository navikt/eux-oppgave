package no.nav.eux.oppgave.webapp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity

class OppgaverApiImplTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST rinasaker - forespørsel, invalid fnr - 400`() {
        val createResponse = restTemplate
            .postForEntity<String>(
                oppgaverUrl,
                oppgaverOpprettelse.httpEntity
            )
        println(createResponse.body)
        assertThat(requestBodies[1].jsonNode).isEqualTo(
            ObjectMapper().readTree(
                javaClass.getResource("/oppgave-opprett.json")!!
                    .readText().trim().lines().joinToString("")
            )
        )
        assertThat(createResponse.statusCode.value()).isEqualTo(201)
    }

    val String.jsonNode: JsonNode get() = ObjectMapper().readTree(this)

}
