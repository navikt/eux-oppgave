package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgavePatch
import no.nav.eux.oppgave.integration.model.Status.FERDIGSTILT
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.UNDER_FERDIGSTILLING
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.stereotype.Service
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.FERDIGSTILT as EUX_FERDIGSTILT

@Service
class FerdigstillService(
    val client: OppgaveClient,
    val exuOppgaveStatusRepository: EuxOppgaveStatusRepository,
) {
    val log = logger {}

    fun ferdigstillOppgaver(journalpostIder: List<String>): List<EuxOppgave> =
        journalpostIder
            .also { log.info { "Starter ferdigstilling av journalposter: $journalpostIder" } }
            .mapNotNull { velgOppgave(it, client.hentOppgaver(it)) }
            .map { client.patch(it.id, OppgavePatch(it.versjon, FERDIGSTILT)) }
            .map { it.euxOppgave }
            .map { it.oppdaterEuxStatus() }

    fun velgOppgave(journalpostId: String, oppgaver: List<Oppgave>): Oppgave? =
        oppgaver
            .firstOrNull { it.status != FERDIGSTILT }
            ?.also { it.settStatusUnderFerdigstilling() }

    fun Oppgave.settStatusUnderFerdigstilling() = exuOppgaveStatusRepository
        .findByOppgaveId(id)
        ?.settUnderFerdigstilling()
        ?: ikkeOpprettetAvEux()

    fun EuxOppgaveStatus.settUnderFerdigstilling() =
        exuOppgaveStatusRepository.save(copy(status = UNDER_FERDIGSTILLING))
            .also { log.info { "Ferdigstiller oppgave opprettet av EUX $oppgaveId" } }

    fun Oppgave.ikkeOpprettetAvEux(): Oppgave {
        log.info { "Ferdigstiller oppgave ikke opprettet av EUX $id" }
        exuOppgaveStatusRepository.save(euxOppgaveStatusUnderFerdigstilling)
        return this
    }

    fun EuxOppgave.oppdaterEuxStatus(): EuxOppgave {
        log.info { "Ferdigstilt oppgave $id" }
        exuOppgaveStatusRepository
            .findByOppgaveId(id)
            ?.also { exuOppgaveStatusRepository.save(it.copy(status = EUX_FERDIGSTILT)) }
        return this
    }
}
