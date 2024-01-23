package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgavePatch
import no.nav.eux.oppgave.integration.model.Status.FERDIGSTILT
import no.nav.eux.oppgave.model.dto.OppgaveFerdigstilling
import no.nav.eux.oppgave.model.dto.OppgaveFerdigstillingStatus.FERDIGSTILLING_FEILET
import no.nav.eux.oppgave.model.dto.OppgaveFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.UNDER_FERDIGSTILLING
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.stereotype.Service
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.FERDIGSTILLING_FEILET as EUX_FERDIGSTILLING_FEILET
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.FERDIGSTILT as EUX_FERDIGSTILT

@Service
class FerdigstillService(
    val client: OppgaveClient,
    val exuOppgaveStatusRepository: EuxOppgaveStatusRepository,
) {
    val log = logger {}

    fun ferdigstillOppgaver(journalpostIder: List<String>): List<OppgaveFerdigstilling> =
        journalpostIder
            .also { log.info { "Starter ferdigstilling av journalposter: $journalpostIder" } }
            .flatMap { client.hentOppgaver(it) }
            .also { log.info { "Ferdigstiller ${it.size} oppgaver" } }
            .also { it.settStatusUnderFerdigstilling() }
            .map { patch(it.id, OppgavePatch(it.versjon, FERDIGSTILT)) }
            .map { it.oppdaterEuxStatus() }

    fun List<Oppgave>.settStatusUnderFerdigstilling() =
        filter { it.status != FERDIGSTILT }
            .forEach { it.settStatusUnderFerdigstilling() }

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

    fun OppgaveFerdigstilling.oppdaterEuxStatus(): OppgaveFerdigstilling {
        if (status == OPPGAVE_FERDIGSTILT) {
            log.info { "Ferdigstilte oppgave ${euxOppgave!!.id}" }
            exuOppgaveStatusRepository
                .findByOppgaveId(euxOppgave!!.id)
                ?.also { exuOppgaveStatusRepository.save(it.copy(status = EUX_FERDIGSTILT)) }
        } else {
            exuOppgaveStatusRepository
                .findByOppgaveId(euxOppgave!!.id)
                ?.also { exuOppgaveStatusRepository.save(it.copy(status = EUX_FERDIGSTILLING_FEILET)) }
        }
        return this
    }

    fun patch(id: Int, patch: OppgavePatch) =
        try {
            val oppgave = client.patch(id, OppgavePatch(patch.versjon, patch.status))
            when (oppgave.status) {
                FERDIGSTILT -> OppgaveFerdigstilling(
                    euxOppgave = oppgave.euxOppgave,
                    status = OPPGAVE_FERDIGSTILT,
                    beskrivelse = "Oppgave ${oppgave.id} ble ferdigstilt"
                )
                else -> OppgaveFerdigstilling(
                    euxOppgave = oppgave.euxOppgave,
                    status = FERDIGSTILLING_FEILET,
                    beskrivelse = "Kall mot oppgave feilet ikke, men oppgave ble ikke ferdigstilt"
                )
            }
        } catch (e: Exception) {
            log.error(e) { "Ferdigstilling av oppgave $id feilet" }
            OppgaveFerdigstilling(
                status = FERDIGSTILLING_FEILET,
                beskrivelse = "Ferdigstilling av oppgave $id feilet pga. manglende respons fra oppgave-systemet"
            )
        }
}
