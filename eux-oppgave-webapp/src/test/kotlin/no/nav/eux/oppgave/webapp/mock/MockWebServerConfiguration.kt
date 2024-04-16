package no.nav.eux.oppgave.webapp.mock

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import jakarta.annotation.PreDestroy
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Instant

@Configuration
class MockWebServerConfiguration(
    val requestBodies: RequestBodies
) {

    val log = logger {}

    private final val server = MockWebServer()

    init {
        server.start(9500)
        server.dispatcher = dispatcher()
    }

    fun mockResponse(request: RecordedRequest) =
        when (request.method) {
            POST.name() -> mockResponsePost(request)
            GET.name() -> mockResponseGet(request)
            PATCH.name() -> mockResponsePatch(request)
            else -> defaultResponse()
        }

    fun mockResponsePost(request: RecordedRequest) =
        when (request.uriEndsWith) {
            "/oauth2/v2.0/token" -> tokenResponse(formParameters(request.body.readUtf8()))
            "/api/v1/oppgaver" -> oppgaverResponse()
            else -> defaultResponse()
        }

    fun mockResponsePatch(request: RecordedRequest) =
        when (request.uriEndsWith) {
            "/api/v1/oppgaver/190402" -> oppgaverResponse()
            else -> defaultResponse()
        }

    fun mockResponseGet(request: RecordedRequest) =
        when (request.uriEndsWith) {
            getOppgaverUri -> getOppgaverResponse()
            getOppgaverUriUtenStatuskategori -> getOppgaverResponse()
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

    fun getOppgaverResponse() =
        MockResponse().apply {
            setResponseCode(200)
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody(getOppgaverResponse)
        }

    val oppgaverResponse = javaClass.getResource("/dataset/oppgave.json")!!.readText()

    val getOppgaverResponse = javaClass.getResource("/dataset/oppgaver.json")!!.readText()

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

    val getOppgaverUri = "/api/v1/oppgaver" +
            "?journalpostId=1234&statuskategori=AAPEN&oppgavetype=JFR&oppgavetype=FDR"

    val getOppgaverUriUtenStatuskategori = "/api/v1/oppgaver" +
            "?journalpostId=453857122&oppgavetype=JFR&oppgavetype=FDR"

    private final fun dispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            log.info { "received ${request.method} ${request.requestUrl} with headers=${request.headers}" }
            requestBodies[request.uriEndsWith] = request.body.readUtf8()
            return mockResponse(request)
        }
    }
}
