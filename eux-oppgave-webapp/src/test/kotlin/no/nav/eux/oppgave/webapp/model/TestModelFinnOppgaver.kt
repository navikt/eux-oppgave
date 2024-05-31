package no.nav.eux.oppgave.webapp.model

import java.time.LocalDate

data class TestModelFinnOppgaver(
    val fristFom: LocalDate,
    val fristTom: LocalDate,
    val tema: String,
    val oppgavetype: String,
    val behandlingstema: String?,
    val behandlingstype: String?,
    val limit: Int?,
    val offset: Int?
)
