package no.nav.eux.oppgave.webapp.mock

import jakarta.annotation.PreDestroy
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Instant

@Configuration
class MockWebServerConfiguration(
    @param:Value("\${mockwebserver.port}") private val port: Int
) {

    val server = MockWebServer()

    init {
        setup()
    }

    private fun setup() {
        server.start(port)
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                log.info("received request on url={} with headers={}", request.requestUrl, request.headers)
                return mockResponse(request)
                    .also { println("respons::    ${it.getBody()}") }
            }
        }
    }

    private fun mockResponse(request: RecordedRequest) =
        if (isTokenRequest(request)) {
            tokenResponse(formParameters(request.body.readUtf8()))
        } else {
            MockResponse().apply {
                setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                setBody(DEFAULT_JSON_RESPONSE)
            }
        }

    fun tokenResponse(formParams: Map<String, String>) =
        MockResponse().apply {
            setResponseCode(200)
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody(tokenResponse)
        }

    @PreDestroy
    fun shutdown() {
        server.shutdown()
    }

    fun isTokenRequest(request: RecordedRequest): Boolean {
        return request.requestUrl.toString().endsWith(tokenEndpointUri) &&
                request.getHeader(CONTENT_TYPE)?.contains(APPLICATION_FORM_URLENCODED_VALUE) ?: false
    }

    fun formParameters(formUrlEncodedString: String) =
        formUrlEncodedString.split("&")
            .filter { it.isNotEmpty() }
            .map { decode(it).split("=", limit = 2) }
            .associate { it[0] to it.getOrElse(1) { "" } }

    fun decode(value: String) = decode(value, UTF_8)

    val tokenResponse = """{
          "token_type": "Bearer",
          "scope": "test",
          "expires_at": "${Instant.now().plusSeconds(3600).epochSecond}",
          "ext_expires_in": "30",
          "expires_in": "30",
          "access_token": "somerandomaccesstoken"
        }""".trimIndent().trim()

    private val DEFAULT_JSON_RESPONSE = """
        {
          "ping": "pong"
        }
        
        """.trimIndent()

    val tokenEndpointUri = "/oauth2/v2.0/token"

    val log: Logger = LoggerFactory.getLogger(MockWebServerConfiguration::class.java)

}