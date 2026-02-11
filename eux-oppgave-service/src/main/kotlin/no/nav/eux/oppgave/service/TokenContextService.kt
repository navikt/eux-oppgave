package no.nav.eux.oppgave.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class TokenContextService {
    val navIdent: String
        get() = (SecurityContextHolder.getContext().authentication?.principal as? Jwt)
            ?.getClaim<String>("NAVident")
            ?: "ukjent"

    val navIdentOrNull: String?
        get() = (SecurityContextHolder.getContext().authentication?.principal as? Jwt)
            ?.getClaim<String>("NAVident")
}
