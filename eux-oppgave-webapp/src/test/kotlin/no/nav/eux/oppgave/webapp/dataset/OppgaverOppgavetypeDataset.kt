package no.nav.eux.oppgave.webapp.dataset

import no.nav.eux.oppgave.openapi.model.EndreOppgavetypeOpenApiType

val oppgaverOppgavetypeDataset = EndreOppgavetypeOpenApiType(
    ider = listOf(190402),
    oppgavetype = "BEH_SED",
    kommentar = "Oppgavetype endret"
)

val oppgaverOppgavetypeFlerDataset = EndreOppgavetypeOpenApiType(
    ider = listOf(999999, 190402),
    oppgavetype = "BEH_SED",
    kommentar = "Oppgavetype endret"
)
