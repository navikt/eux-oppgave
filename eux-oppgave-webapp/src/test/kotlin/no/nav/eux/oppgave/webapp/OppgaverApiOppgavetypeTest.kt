package no.nav.eux.oppgave.webapp

import no.nav.eux.oppgave.webapp.common.*
import no.nav.eux.oppgave.webapp.dataset.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OppgaverApiOppgavetypeTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `PATCH oppgaver oppgavetype - forespørsel, valid - 204`() {
        restTestClient
            .patch()
            .uri(oppgaverOppgavetypeUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOppgavetypeDataset)
            .exchange()
            .expectStatus().isEqualTo(204)
        val requestBody = requestBodies["/api/v1/oppgaver/190402"]!!
        val result = requestBody.jsonNode
        assertThat(result["versjon"].intValue()).isEqualTo(4)
        assertThat(result["oppgavetype"].stringValue()).isEqualTo("BEH_SED")
        assertThat(result["kommentar"]["tekst"].stringValue()).isEqualTo("Oppgavetype endret")
        assertThat(result["beskrivelse"].stringValue()).contains("Oppgavetype endret")
    }

    @Test
    fun `PATCH oppgaver oppgavetype - forespørsel, uten kommentar - 204`() {
        restTestClient
            .patch()
            .uri(oppgaverOppgavetypeUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOppgavetypeUtenKommentarDataset)
            .exchange()
            .expectStatus().isEqualTo(204)
        val result = requestBodies["/api/v1/oppgaver/190402"]!!.jsonNode
        assertThat(result["kommentar"]["tekst"].stringValue())
            .isEqualTo("Oppgavetype endret til BEH_SED")
        assertThat(result["beskrivelse"].stringValue())
            .contains("Oppgavetype endret til BEH_SED")
    }

    @Test
    fun `PATCH oppgaver oppgavetype - forespørsel, ekstern feil på første oppgave, fortsetter - 204`() {
        restTestClient
            .patch()
            .uri(oppgaverOppgavetypeUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOppgavetypeFlerDataset)
            .exchange()
            .expectStatus().isEqualTo(204)
        val feiletPatch = requestBodies["/api/v1/oppgaver/999999"]!!.jsonNode
        val vellykketPatch = requestBodies["/api/v1/oppgaver/190402"]!!.jsonNode
        assertThat(feiletPatch["oppgavetype"].stringValue()).isEqualTo("BEH_SED")
        assertThat(vellykketPatch["oppgavetype"].stringValue()).isEqualTo("BEH_SED")
    }
}
