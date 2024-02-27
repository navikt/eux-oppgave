package no.nav.eux.oppgave.service

import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

infix fun String?.med(nyBeskrivelse: OppgaveBeskrivelse): String {
    val formatter = ofPattern("dd.MM.yyyy HH:mm")
    val output = formatter.format(now())
    return "--- $output (${nyBeskrivelse.navIdent}) ---\n${nyBeskrivelse.tekst}\n\n${this ?: ""}"
}

data class OppgaveBeskrivelse(
    val navIdent: String,
    val tekst: String
)
