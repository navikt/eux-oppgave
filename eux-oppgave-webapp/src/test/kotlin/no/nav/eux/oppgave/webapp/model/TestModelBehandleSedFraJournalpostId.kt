package no.nav.eux.oppgave.webapp.model

data class TestModelBehandleSedFraJournalpostId(
    val journalpostId: String
)

data class TestModelBehandleSedFraJournalpostIdMedAktoerId(
    val journalpostId: String,
    val personident: String
)
