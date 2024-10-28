package no.nav.eux.oppgave.integration.model.patch

data class OppgaveTildeltEnhetsnrPatch(
    val versjon: Int,
    val tilordnetRessurs: String? = null,
    val tildeltEnhetsnr: String,
    val kommentar: OppgavePatchKommentar,
    val beskrivelse: String,
)
