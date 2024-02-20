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
import java.time.LocalDateTime.now
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.FERDIGSTILLING_FEILET as EUX_FERDIGSTILLING_FEILET
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.FERDIGSTILT as EUX_FERDIGSTILT

@Service
class FerdigstillService(
    val client: OppgaveClient,
    val statusRepository: EuxOppgaveStatusRepository,
    val contextService: TokenContextService,
) {
    val log = logger {}

    fun ferdigstillOppgaver(
        journalpostIder: List<String>,
        personident: String?
    ): List<OppgaveFerdigstilling> =
        journalpostIder
            .also { log.info { "Starter ferdigstilling av journalposter: $journalpostIder" } }
            .flatMap { client.hentOppgaver(it) }
            .also { log.info { "Ferdigstiller ${it.size} oppgaver" } }
            .also { it.settStatusUnderFerdigstilling() }
            .map { patch(it.id, OppgavePatch(it.versjon, contextService.navIdent, FERDIGSTILT, personident)) }
            .map { it.oppdaterEuxStatus() }

    fun List<Oppgave>.settStatusUnderFerdigstilling() =
        filter { it.status != FERDIGSTILT }
            .forEach { it.settStatusUnderFerdigstilling() }

    fun Oppgave.settStatusUnderFerdigstilling() = statusRepository
        .findByOppgaveId(id)
        ?.settUnderFerdigstilling()
        ?: ikkeOpprettetAvEux()

    fun EuxOppgaveStatus.settUnderFerdigstilling() =
        statusRepository.save(copy(status = UNDER_FERDIGSTILLING, endretTidspunkt = now()))
            .also { log.info { "Ferdigstiller oppgave opprettet av EUX ($oppgaveId)" } }

    fun Oppgave.ikkeOpprettetAvEux(): Oppgave {
        log.info { "Ferdigstiller oppgave ikke opprettet av EUX ($id)" }
        statusRepository.save(toEuxOppgaveStatusUnderFerdigstilling(contextService.navIdent))
        return this
    }

    fun OppgaveFerdigstilling.oppdaterEuxStatus(): OppgaveFerdigstilling {
        if (status == OPPGAVE_FERDIGSTILT) {
            log.info { "Ferdigstilte oppgave ${euxOppgave!!.id}" }
            saveExuOppgaveStatus(euxOppgave!!.id, EUX_FERDIGSTILT)
        } else {
            saveExuOppgaveStatus(euxOppgave!!.id, EUX_FERDIGSTILLING_FEILET)
        }
        return this
    }

    fun saveExuOppgaveStatus(oppgaveId: Int, status: EuxOppgaveStatus.Status) =
        statusRepository
            .findByOppgaveId(oppgaveId = oppgaveId)
            ?.also {
                statusRepository.save(
                    it.copy(
                        status = status,
                        endretTidspunkt = now(),
                        endretBruker = contextService.navIdent
                    )
                )
            }

    fun patch(id: Int, patch: OppgavePatch) =
        try {
            val oppgave = client.patch(id, patch)
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
