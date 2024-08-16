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
import java.time.LocalDate
import java.time.LocalDate.now

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

    fun mockResponse(request: RecordedRequest, body: String) =
        when (request.method) {
            POST.name() -> mockResponsePost(request, body)
            GET.name() -> mockResponseGet(request)
            PATCH.name() -> mockResponsePatch(request, body)
            else -> defaultResponse()
        }

    fun mockResponsePost(request: RecordedRequest, body: String) =
        when (request.uriEndsWith) {
            "/oauth2/v2.0/token" -> tokenResponse(formParameters(request.body.readUtf8()))
            "/api/v1/oppgaver" -> oppgaverResponse(body)
            else -> defaultResponse()
        }

    fun mockResponsePatch(request: RecordedRequest, body: String) =
        when (request.uriEndsWith) {
            "/api/v1/oppgaver/190402" -> oppgaverResponse(body)
            else -> defaultResponse()
        }

    fun mockResponseGet(request: RecordedRequest) =
        when (request.uriEndsWith) {
            getOppgaverUri(1234, "AAPEN") -> getOppgaverResponse()
            getOppgaverUri(453857122, "AAPEN") -> getOppgaverResponse()
            getOppgaverUri(453857122, "AVSLUTTET") -> getOppgaverResponse()
            getOppgaverUri(453857123, "AAPEN", "JFR") -> getOppgaverResponseEmpty()
            finnOppgaverUriBehandlingstema("BAR", "FREM", "AAPEN", "ab0058", 200, 10) -> getOppgaverResponse()
            finnOppgaverUriBehandlingstype("BAR", "FREM", "AAPEN", "ae0106") -> getOppgaverResponse()
            else -> defaultResponse()
        }

    fun defaultResponse() =
        MockResponse().apply {
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody("""{"ping": "pong"}""")
        }

    val RecordedRequest.uriEndsWith get() = requestUrl.toString().split("/mock")[1]

    fun oppgaverResponse(body: String) =
        if (body.contains("feilmelding"))
            MockResponse().apply {
                setResponseCode(400)
                setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                setBody(oppgaverResponseFeilmelding)
            }
        else
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

    fun getOppgaverResponseEmpty() =
        MockResponse().apply {
            setResponseCode(200)
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody(getOppgaverResponseEmpty)
        }
            .also { log.info { "--------------------- why?" } }

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

    fun getOppgaverUri(
        journalpostId: Int,
        statuskategori: String
    ) = "/api/v1/oppgaver" +
            "?journalpostId=$journalpostId&statuskategori=$statuskategori&oppgavetype=JFR&oppgavetype=FDR"

    fun getOppgaverUri(
        journalpostId: Int,
        statuskategori: String,
        oppgavetype: String
    ) = "/api/v1/oppgaver" +
            "?journalpostId=$journalpostId&statuskategori=$statuskategori&oppgavetype=$oppgavetype"

    fun finnOppgaverUriBehandlingstema(
        tema: String,
        oppgavetype: String,
        statuskategori: String,
        behandlingstema: String,
        limit: Int,
        offset: Int,
        fristFom: LocalDate = now(),
        fristTom: LocalDate = now(),
    ) = "/api/v1/oppgaver" +
            "?fristFom=$fristFom&fristTom=$fristTom&tema=$tema&oppgavetype=" +
            "$oppgavetype&statuskategori=$statuskategori" +
            "&behandlingstema=$behandlingstema&limit=$limit&offset=$offset"

    fun finnOppgaverUriBehandlingstype(
        tema: String,
        oppgavetype: String,
        statuskategori: String,
        behandlingstype: String,
        fristFom: LocalDate = now(),
        fristTom: LocalDate = now(),
    ) = "/api/v1/oppgaver" +
            "?fristFom=$fristFom&fristTom=$fristTom&tema=$tema&oppgavetype=" +
            "$oppgavetype&statuskategori=$statuskategori" +
            "&behandlingstype=$behandlingstype"

    private final fun dispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            log.info { "received ${request.method} ${request.requestUrl} with headers=${request.headers}" }
            val body = request.body.readUtf8()
            requestBodies[request.uriEndsWith] = body
            return mockResponse(request, body)
        }
    }
}
