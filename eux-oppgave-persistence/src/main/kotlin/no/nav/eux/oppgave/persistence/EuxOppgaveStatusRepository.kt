package no.nav.eux.oppgave.persistence

import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EuxOppgaveStatusRepository : JpaRepository<EuxOppgaveStatus, UUID> {
    fun findByOppgaveId(oppgaveId: Int?): EuxOppgaveStatus?
}
