package no.nav.eux.oppgave.integration.model.patch

data class OppgavePatchKommentar(
    val tekst: String,
    val automatiskGenerert: Boolean = false
)