package no.nav.eux.oppgave.integration.config

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import kotlin.jvm.optionals.getOrNull

@Component
class DualOppgaveRestTemplate(
    val tokenValidationContextHolder: TokenValidationContextHolder,
    val oppgaveRestTemplatePrivateKeyJwt: RestTemplate,
    val oppgaveRestTemplateClientSecretBasic: RestTemplate,
) {

    fun restClient() = RestClient.create(restTemplate())

    fun post() = restClient().post()

    fun patch() = restClient().patch()

    fun get() = restClient().get()

    fun restTemplate() =
        when (contextHasNavIdent()) {
            true -> oppgaveRestTemplateClientSecretBasic
            false -> oppgaveRestTemplatePrivateKeyJwt
        }

    fun contextHasNavIdent(): Boolean =
        tokenValidationContextHolder
            .tokenValidationContext
            ?.firstValidToken
            ?.getOrNull()
            ?.jwtTokenClaims
            ?.get("NAVident") != null
}
