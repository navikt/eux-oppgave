package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.advice.MethodArgumentNotValidExceptionAdvice
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.common.token
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OppgaverApiValidationTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - forespørsel, invalid tildeltEnhetsnr - 400`() {
        val createResponse = restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelse.copy(tildeltEnhetsnr = "12345"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(MethodArgumentNotValidExceptionAdvice.ApiError::class.java)
            .returnResult()
        with(createResponse.responseBody!!.errors[0]) {
            assertThat(rejectedValue).isEqualTo("12345")
            assertThat(defaultMessage).isEqualTo("size must be between 4 and 4")
        }
    }
}
