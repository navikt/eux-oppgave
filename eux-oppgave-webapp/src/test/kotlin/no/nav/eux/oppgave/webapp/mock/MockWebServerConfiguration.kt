package no.nav.eux.oppgave.webapp.mock

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import jakarta.annotation.PreDestroy
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Instant

@Configuration
class MockWebServerConfiguration(
    private val server: MockWebServer = MockWebServer()
) {
    val log = logger {}

    init {
        server.start(9500)
        server.dispatcher = dispatcher()
    }

    fun mockResponse(request: RecordedRequest) =
        when (request.uriEndsWith) {
            "/oauth2/v2.0/token" -> tokenResponse(formParameters(request.body.readUtf8()))
            "/api/v1/oppgaver" -> oppgaverResponse()
            else -> defaultResponse()
        }

    fun defaultResponse() =
        MockResponse().apply {
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody("""{"ping": "pong"}""")
        }

    val RecordedRequest.uriEndsWith get() = requestUrl.toString().split("/mock")[1]

    fun oppgaverResponse() =
        MockResponse().apply {
            setResponseCode(200)
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody(oppgaverResponse)
        }

    val oppgaverResponse = javaClass.getResource("/oppgaver-opprettelse-respons.json")!!.readText()

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

    fun formParameters(formUrlEncodedString: String) =
        formUrlEncodedString.split("&")
            .filter { it.isNotEmpty() }
            .map { decode(it).split("=", limit = 2) }
            .associate { it[0] to it.getOrElse(1) { "" } }

    fun decode(value: String): String = decode(value, UTF_8)

    val tokenResponse = """{
          "token_type": "Bearer",
          "scope": "test",
          "expires_at": "${Instant.now().plusSeconds(3600).epochSecond}",
          "ext_expires_in": "30",
          "expires_in": "30",
          "access_token": "token"
        }"""

    private final fun dispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            log.info { "received request on url=${request.requestUrl} with headers=${request.headers}" }
            return mockResponse(request)
        }
    }
}