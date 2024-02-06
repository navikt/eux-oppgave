package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.advice.MethodArgumentNotValidExceptionAdvice
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity

class OppgaverApiValidationTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - forespørsel, invalid tildeltEnhetsnr - 400`() {
        val createResponse = restTemplate
            .postForEntity<MethodArgumentNotValidExceptionAdvice.ApiError>(
                oppgaverUrl,
                oppgaverOpprettelse.copy(tildeltEnhetsnr = "12345").httpEntity
            )
        assertThat(createResponse.statusCode.value()).isEqualTo(400)
        with(createResponse.body!!.errors[0]) {
            assertThat(rejectedValue).isEqualTo("12345")
            assertThat(defaultMessage).isEqualTo("size must be between 4 and 4")
        }
    }
}
