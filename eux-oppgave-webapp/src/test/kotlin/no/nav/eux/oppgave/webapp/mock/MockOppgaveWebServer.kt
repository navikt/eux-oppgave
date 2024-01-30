package no.nav.eux.oppgave.webapp.mock

import jakarta.annotation.PreDestroy
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets.UTF_8

@Configuration
class MockOppgaveWebServer(
) {

    val server = MockWebServer()

    init {
        setup()
    }

    private final fun setup() {
        server.start(9400)
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                log.info("received request on url={} with headers={}", request.requestUrl, request.headers)
                return mockResponse(request)
                    .also { println("oppgaver respons::    ${it.getBody()}") }
            }
        }
    }

    fun mockResponse(request: RecordedRequest) =
        if (isOppgaverRequest(request)) {
            oppgaverResponse(formParameters(request.body.readUtf8()))
        } else {
            MockResponse().apply {
                setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                setBody(defaultJsonRespons)
            }
        }

    fun oppgaverResponse(formParams: Map<String, String>) =
        MockResponse().apply {
            setResponseCode(200)
            setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            setBody(oppgaverResponse)
        }

    @PreDestroy
    fun shutdown() {
        server.shutdown()
    }

    fun isOppgaverRequest(request: RecordedRequest): Boolean {
        return request.requestUrl.toString().endsWith(oppgaverEndpointUri) &&
                request.getHeader(CONTENT_TYPE)?.contains(APPLICATION_JSON_VALUE) ?: false
    }

    fun formParameters(formUrlEncodedString: String) =
        formUrlEncodedString.split("&")
            .filter { it.isNotEmpty() }
            .map { decode(it).split("=", limit = 2) }
            .associate { it[0] to it.getOrElse(1) { "" } }

    fun decode(value: String) = decode(value, UTF_8)

    val oppgaverResponse = """
    {
      "id": 190402,
      "oppgavetype": "JFR",
      "tildeltEnhetsnr": "2950",
      "endretAvEnhetsnr": "2950",
      "opprettetAvEnhetsnr": "9999",
      "journalpostId": "453857122",
      "aktoerId": "2850955164683",
      "tilordnetRessurs": "Z993030",
      "beskrivelse": "desc",
      "tema": "GEN",
      "versjon": 4,
      "opprettetAv": "srveux-app",
      "endretAv": "eux-oppgave-q2",
      "prioritet": "NORM",
      "status": "FERDIGSTILT",
      "metadata": {
        "RINA_SAKID": "1447222"
      },
      "fristFerdigstillelse": "2024-01-16",
      "aktivDato": "2024-01-15",
      "opprettetTidspunkt": "2024-01-15T11:01:02.96+01:00",
      "ferdigstiltTidspunkt": "2024-01-17T18:17:23.976+01:00",
      "endretTidspunkt": "2024-01-17T18:17:23.976+01:00"
    }""".trimIndent().trim()

    val defaultJsonRespons = """{"ping": "pong"}""".trimIndent()

    val oppgaverEndpointUri = "/api/v1/oppgaver"

    val log: Logger = LoggerFactory.getLogger(MockOppgaveWebServer::class.java)

}