package no.nav.eux.oppgave.webapp.mock

val oppgaverResponse = getResource("/dataset/response/oppgave.json")

val oppgaverResponseFeilmelding = getResource("/dataset/response/oppgave-feilmelding.json")

val getOppgaverResponse = getResource("/dataset/response/oppgaver.json")

val getOppgaverResponseEmpty = getResource("/dataset/response/oppgaver-empty.json")

val getOppgaverResponseBehSed = getResource("/dataset/response/oppgaver-beh-sed.json")

val getOppgaverResponseFeilet = getResource("/dataset/response/oppgaver-feilet.json")

val getOppgaveResponse999999 = getResource("/dataset/response/oppgave-999999.json")

val getOppgaverResponseIkkeFerdigstilt = getResource("/dataset/response/oppgaver-ikke-ferdigstilt.json")

val getOppgaveResponseIkkeFerdigstilt = getResource("/dataset/response/oppgave-ikke-ferdigstilt.json")

fun getResource(resource: String) = Any::class::class.java.getResource(resource)!!.readText()
