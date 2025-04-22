package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.client.OpprettOppgaveRetryableClient
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTELSE_FEILET
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTET
import no.nav.eux.oppgave.model.exception.OppgaveStatusEksistererException
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class OppgaveService(
    val client: OppgaveClient,
    val retryableClient: OpprettOppgaveRetryableClient,
    val oppgaveStatusRepository: EuxOppgaveStatusRepository,
) {
    val log = logger {}

    fun opprettOppgave(euxOppgaveOpprettelse: EuxOppgaveOpprettelse): EuxOppgave {
        euxOppgaveOpprettelse.sjekkStatus()
        val euxOppgaveStatus = oppgaveStatusRepository.save(euxOppgaveOpprettelse.euxOppgaveStatus)
        try {
            val oppgave = when (euxOppgaveOpprettelse.lagNestenLikOppgave) {
                true -> client.opprettOppgave(euxOppgaveOpprettelse.oppgaveOpprettelse)
                false -> retryableClient.opprettUnikOppgave(euxOppgaveOpprettelse.oppgaveOpprettelse)
            }
            oppgaveStatusRepository.save(euxOppgaveStatus.copy(oppgaveId = oppgave.id, status = OPPRETTET))
            log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
            return oppgave.euxOppgave
        } catch (e: DataIntegrityViolationException) {
            val message = e.message
            if (message != null && message.contains("duplicate key value violates unique constraint")) {
                log.error(e) { "Oppgavestatus med unik verdi finnes allerede" }
                throw OppgaveStatusEksistererException("Oppgavestatus finnes allerede")
            }
            throw e
        } catch (e: Exception) {
            log.error(e) { "Oppgaveopprettelse feilet" }
            oppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTELSE_FEILET))
            throw e
        }
    }

    fun behandleSedFraJournalpostId(journalpostId: String, personident: String?): EuxOppgave {
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
        val personidentNyOppgave = personident ?: eksisterendeOppgave.aktoerId
        val behandleSedOppgave = eksisterendeOppgave.copy(oppgavetype = "BEH_SED", aktoerId = personidentNyOppgave)
        log.info { "Eksisterende oppgave: $eksisterendeOppgave" }
        log.info { "Behandle SED oppgave: $behandleSedOppgave" }
        val euxOppgaveStatus = oppgaveStatusRepository.save(behandleSedOppgave.euxOppgaveStatus)
        try {
            val oppgave = client.opprettOppgave(behandleSedOppgave.oppgaveOpprettelse)
            oppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTET))
            log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
            return oppgave.euxOppgave
        } catch (e: Exception) {
            log.error(e) { "Oppgaveopprettelse feilet. journalpostId=${behandleSedOppgave.journalpostId}" }
            oppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTELSE_FEILET))
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

    fun EuxOppgaveOpprettelse.sjekkStatus() {
        if (oppgaveUuid != null) {
            val eksisterendeStatus = oppgaveStatusRepository.findByIdOrNull(oppgaveUuid!!)

            if (eksisterendeStatus != null && eksisterendeStatus.status != OPPRETTELSE_FEILET) {
                log.warn { "Oppgavestatus med id $oppgaveUuid finnes allerede" }
                throw OppgaveStatusEksistererException.oppgaveEksisterer
            } else if (eksisterendeStatus != null) {
                log.warn { "Retry med tidligere feilet opprettelse" }
            }
        }
    }
}
