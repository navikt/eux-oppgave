package no.nav.eux.oppgave.model.exception

class OppgaveStatusEksistererException(
    override val message: String,
) : RuntimeException(message) {

    companion object {
        val oppgaveEksisterer =
            OppgaveStatusEksistererException("Det eksisterer allerede en oppgavestatus med gitt uuid")
    }
}
