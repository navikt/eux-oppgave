package no.nav.eux.oppgave.model.common

inline fun <reified T : Enum<T>> String.toEnum() =
    enumValues<T>().firstOrNull { it.name.uppercase() == this.uppercase() }
        ?: throw RuntimeException(
            "Invalid value $this for ${T::class.simpleName}. " +
                    "It must be one of the following: ${enumValues<T>().contentToString()}"
        )
