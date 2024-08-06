package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTELSE_FEILET
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTET
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class OppgaveService(
    val client: OppgaveClient,
    val exuOppgaveStatusRepository: EuxOppgaveStatusRepository,
) {
    val log = logger {}

    fun opprettOppgave(euxOppgaveOpprettelse: EuxOppgaveOpprettelse): EuxOppgave {
        log.info { "Oppretter oppgave..." }
        val euxOppgaveStatus = exuOppgaveStatusRepository.save(euxOppgaveOpprettelse.euxOppgaveStatus)
        try {
            val oppgave = client.opprettOppgave(euxOppgaveOpprettelse.oppgaveOpprettelse)
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(oppgaveId = oppgave.id, status = OPPRETTET))
            log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
            return oppgave.euxOppgave
        } catch (e: Exception) {
            log.error(e) { "Oppgaveopprettelse feilet" }
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTELSE_FEILET))
            throw e
        }
    }

    fun behandleSedFraJournalpostId(journalpostId: String): EuxOppgave {
        val oppgaverAapen = client.hentOppgaver(journalpostId = journalpostId, statuskategori = "AAPEN")
        val oppgaverAvsluttet = client.hentOppgaver(journalpostId = journalpostId, statuskategori = "AVSLUTTET")
        log.info { "Journalposten har ${oppgaverAapen.size} åpne oppgaver" }
        log.info { "Journalposten har ${oppgaverAvsluttet.size} avsluttede oppgaver" }
        val oppgaver = oppgaverAapen + oppgaverAvsluttet
        oppgaver
            .firstOrNull { it.oppgavetype == "BEH_SED" }
            ?.let {
                log.info { "Oppgave for journalpostId=$journalpostId finnes allerede" }
                return it.euxOppgave
            }
        val eksisterendeOppgave = oppgaver
            .firstOrNull()
            ?: throw IllegalArgumentException("Fant ingen oppgaver for journalpostId=$journalpostId")
        val behandleSedOppgave = eksisterendeOppgave.copy(oppgavetype = "BEH_SED")
        log.info { "Eksisterende oppgave: $eksisterendeOppgave" }
        log.info { "Behandle SED oppgave: $behandleSedOppgave" }
        val euxOppgaveStatus = exuOppgaveStatusRepository.save(behandleSedOppgave.euxOppgaveStatus)
        try {
            val oppgave = client.opprettOppgave(behandleSedOppgave.oppgaveOpprettelse)
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTET))
            log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
            return oppgave.euxOppgave
        } catch (e: Exception) {
            log.error(e) { "Oppgaveopprettelse feilet. journalpostId=${behandleSedOppgave.journalpostId}" }
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTELSE_FEILET))
            throw e
        }
    }

    fun finnOppgaver(
        fristFom: LocalDate,
        fristTom: LocalDate,
        tema: String,
        oppgavetype: String,
        behandlingstema: String?,
        behandlingstype: String?,
        limit: Int?,
        offset: Int?
    ): List<EuxOppgave> {
        val oppgaver = client.finnOppgaver(
            fristFom,
            fristTom,
            tema,
            oppgavetype,
            statuskategori = "AAPEN",
            behandlingstema,
            behandlingstype,
            limit,
            offset
        )
        log.info { "Fant ${oppgaver.size} oppgaver" }
        return oppgaver.map { it.euxOppgave }
    }

    fun oppdaterOppgave(euxOppgaveOppdatering: EuxOppgave): EuxOppgave =
        try {
            client
                .patch(euxOppgaveOppdatering.id, euxOppgaveOppdatering.oppgave)
                .euxOppgave
        } catch (e: Exception) {
            log.error(e) { "Oppdatering av oppgave ${euxOppgaveOppdatering.id} feilet" }
            euxOppgaveOppdatering
        }
}
