package no.nav.eux.oppgave.integration.config

import com.nimbusds.jose.jwk.JWK
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.boot.restclient.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.annotation.EnableRetry
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.client.RestTemplate
import java.util.UUID.randomUUID

@EnableRetry
@Configuration
class IntegrationConfig {

    val log = logger {}

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService
    ): OAuth2AuthorizedClientManager {
        val tokenResponseClient = RestClientClientCredentialsTokenResponseClient()
        tokenResponseClient.addParametersConverter(
            NimbusJwtClientAuthenticationParametersConverter<OAuth2ClientCredentialsGrantRequest> { _ ->
                JWK.parse(System.getenv("AZURE_APP_JWK"))
            }
        )
        val clientCredentialsProvider = ClientCredentialsOAuth2AuthorizedClientProvider()
        clientCredentialsProvider.setAccessTokenResponseClient(tokenResponseClient)
        val authorizedClientProvider = DelegatingOAuth2AuthorizedClientProvider(
            clientCredentialsProvider,
            JwtBearerOAuth2AuthorizedClientProvider()
        )
        val manager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        )
        manager.setAuthorizedClientProvider(authorizedClientProvider)
        return manager
    }

    @Bean
    fun oppgaveRestTemplateClientSecretBasic(
        restTemplateBuilder: RestTemplateBuilder,
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): RestTemplate =
        restTemplateBuilder
            .additionalInterceptors(bearerTokenInterceptor(authorizedClientManager, "oppgave-credentials-client-secret-basic"))
            .build()

    @Bean
    fun oppgaveRestTemplatePrivateKeyJwt(
        restTemplateBuilder: RestTemplateBuilder,
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): RestTemplate =
        restTemplateBuilder
            .additionalInterceptors(bearerTokenInterceptor(authorizedClientManager, "oppgave-credentials-private-key-jwt"))
            .build()

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
        authorizedClientManager: OAuth2AuthorizedClientManager,
        registrationId: String
    ) = ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
        val authorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(registrationId)
            .principal("eux-oppgave")
            .build()
        val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
        request.headers.setBearerAuth(authorizedClient!!.accessToken.tokenValue)
        request.headers.set("X-Correlation-ID", randomUUID().toString())
        execution.execute(request, body)
    }
}
