package no.nav.eux.oppgave.integration.model.patch

data class OppgaveTypePatch(
    val versjon: Int,
    val oppgavetype: String,
    val kommentar: OppgavePatchKommentar,
    val beskrivelse: String,
)
