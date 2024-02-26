package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgavePatchKommentar
import no.nav.eux.oppgave.integration.model.OppgaveTildeltEnhetsnrPatch
import no.nav.eux.oppgave.integration.model.Status
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.*
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class TildelEnhetsnummerService(
    val client: OppgaveClient,
    val statusRepository: EuxOppgaveStatusRepository,
    val contextService: TokenContextService,
) {
    val log = logger {}

    fun tildelEnhetsnummer(
        journalpostId: String,
        tildeltEnhetsnr: String,
        kommentar: String
    ) {
        client
            .hentOppgaver(journalpostId)
            .also { log.info { "Tildeler enhetsnummer for ${it.size} oppgaver" } }
            .also { it.settStatusTildelerEnhetsnummer() }
            .forEach { it.patch(tildeltEnhetsnr, kommentar) }
    }

    fun List<Oppgave>.settStatusTildelerEnhetsnummer() =
        filter { it.status != Status.FERDIGSTILT }
            .forEach { it.settStatusTildelerEnhetsnummer() }

    fun Oppgave.settStatusTildelerEnhetsnummer() = statusRepository
        .findByOppgaveId(id)
        ?.settStatusTildelerEnhetsnummer()
        ?: ikkeOpprettetAvEux()

    fun EuxOppgaveStatus.settStatusTildelerEnhetsnummer() =
        statusRepository.save(copy(status = TILDELER_ENHETSNR, endretTidspunkt = now()))
            .also { log.info { "Tildeler enhetsnummer for oppgave opprettet av EUX $oppgaveId" } }

    fun Oppgave.ikkeOpprettetAvEux(): Oppgave {
        log.info { "Tildeler enhetsnummer for oppgave ikke opprettet av EUX $id" }
        statusRepository.save(toEuxOppgaveTildelerEnhetsnummer(contextService.navIdent))
        return this
    }

    fun Oppgave.patch(tildeltEnhetsnr: String, kommentar: String) =
        try {
            client.patch(id, OppgaveTildeltEnhetsnrPatch(
                versjon = versjon,
                tildeltEnhetsnr = tildeltEnhetsnr,
                kommentar = OppgavePatchKommentar(tekst = kommentar)
            ))
            saveExuOppgaveStatus(id, ENHETSNR_TILDELT)
        } catch (e: Exception) {
            saveExuOppgaveStatus(id, TILDEL_ENHETSNR_FEILET)
            throw e
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
}
