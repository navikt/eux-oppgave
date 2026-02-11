package no.nav.eux.oppgave.integration.config

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

@Component
class DualOppgaveRestTemplate(
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
        (SecurityContextHolder.getContext().authentication?.principal as? Jwt)
            ?.getClaim<String>("NAVident") != null
}
