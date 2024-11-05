package no.nav.eux.oppgave.integration.config

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.client.RestTemplate
import java.util.UUID.randomUUID

@EnableRetry
@EnableOAuth2Client(cacheEnabled = true)
@Configuration
class IntegrationConfig {

    val log = logger {}

    @Bean
    fun oppgaveRestTemplateClientSecretBasic(
        restTemplateBuilder: RestTemplateBuilder,
        clientConfigurationProperties: ClientConfigurationProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ): RestTemplate {
        val clientProperties: ClientProperties = clientConfigurationProperties
            .registration["oppgave-credentials-client-secret-basic"]
            ?: throw RuntimeException("could not find oauth2 client config for oppgave-credentials-client-secret-basic")
        return restTemplateBuilder
            .additionalInterceptors(bearerTokenInterceptor(clientProperties, oAuth2AccessTokenService))
            .build()
    }

    @Bean
    fun oppgaveRestTemplatePrivateKeyJwt(
        restTemplateBuilder: RestTemplateBuilder,
        clientConfigurationProperties: ClientConfigurationProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ): RestTemplate {
        val clientProperties: ClientProperties = clientConfigurationProperties
            .registration["oppgave-credentials-private-key-jwt"]
            ?: throw RuntimeException("could not find oauth2 client config for oppgave-credentials-private-key-jwt")
        return restTemplateBuilder
            .additionalInterceptors(bearerTokenInterceptor(clientProperties, oAuth2AccessTokenService))
            .build()
    }

    @Bean
    fun retryListener() = object : RetryListener {
        override fun <T, E : Throwable?> onError(
            context: RetryContext,
            callback: RetryCallback<T, E>,
            throwable: Throwable
        ) {
            log.warn(throwable) {
                "Eksternt kall feilet: ${context.getAttribute("context.name")}, forsøk nr: ${context.retryCount}"
            }
        }
    }

    private fun bearerTokenInterceptor(
        clientProperties: ClientProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ) = ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        request.headers.setBearerAuth(response.access_token!!)
        request.headers.set("X-Correlation-ID", randomUUID().toString())
        execution.execute(request, body)
    }
}
