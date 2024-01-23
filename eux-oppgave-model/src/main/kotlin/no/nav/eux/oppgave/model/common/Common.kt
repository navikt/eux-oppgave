package no.nav.eux.oppgave.model.common

inline fun <reified T : Enum<T>> String.toEnum() =
    enumValues<T>().firstOrNull { it.name.uppercase().replace("_", "") == this.uppercase().replace("_", "") }
        ?: throw RuntimeException(
            "Invalid value $this for ${T::class.simpleName}. " +
                    "It must be one of the following: ${enumValues<T>().contentToString()}"
        )
