package no.nav.eux.oppgave.service

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class TokenContextService(
    val tokenValidationContextHolder: TokenValidationContextHolder
) {
    val navIdent
        get() = tokenValidationContextHolder
            .tokenValidationContext
            ?.firstValidToken
            ?.getOrNull()
            ?.jwtTokenClaims
            ?.get("NAVident")
            ?.toString()
            ?: "ukjent"
}
