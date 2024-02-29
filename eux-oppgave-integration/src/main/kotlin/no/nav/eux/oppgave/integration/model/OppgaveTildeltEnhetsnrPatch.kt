package no.nav.eux.oppgave.integration.model

data class OppgaveTildeltEnhetsnrPatch(
    val versjon: Int,
    val tilordnetRessurs: String? = null,
    val tildeltEnhetsnr: String,
    val kommentar: OppgavePatchKommentar,
    val beskrivelse: String,
)

data class OppgavePatchKommentar(
    val tekst: String,
    val automatiskGenerert: Boolean = false
)
