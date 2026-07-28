package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.webapp.model.TestModelOppgaverFerdigstill

val oppgaverFerdigstillDataset = TestModelOppgaverFerdigstill(
    journalpostIder = listOf("1234"),
    personident = "2850955164683"
)

val oppgaverFerdigstillFeiletDataset = TestModelOppgaverFerdigstill(
    journalpostIder = listOf("333333"),
    personident = null
)

val oppgaverFerdigstillDelvisFeiletDataset = TestModelOppgaverFerdigstill(
    journalpostIder = listOf("1234", "222222"),
    personident = "2850955164683"
)
