### EUX Oppgave

Integrasjoner mellom EUX og NAV Oppgave

## Brukte teknologier
* Kotlin
* Spring
* Maven

#### Avhengigheter

* JDK 25

#### Kjøring av tester lokalt

Kjøring av database-tester krever en kjørende PostgreSQL database med følgende variabler satt korrekt:

```
set -x DATABASE_HOST localhost
set -x DATABASE_USERNAME postgres
set -x DATABASE_DATABASE postgres
set -x DATABASE_PORT 5432
```

#### Kjøring av tester lokalt på macOS med PostgreSQL via Homebrew

Installer og start PostgreSQL:

```bash
brew install postgresql@18
brew services start postgresql@18
```

Med Homebrew er superbrukeren din macOS-bruker, og lokale tilkoblinger bruker
trust-autentisering (tomt passord). Sett miljøvariablene i bash eller zsh:

```bash
export DATABASE_HOST=localhost
export DATABASE_PORT=5432
export DATABASE_DATABASE=postgres
export DATABASE_USERNAME=$(whoami)
export DATABASE_PASSWORD=""
```

`DATABASE_PASSWORD` må være definert, men kan være tom. Bygget henter
`eux-parent-pom` fra GitHub Packages, så `READER_TOKEN` må også være satt
(se `.github/settings.xml`). Kjør deretter testene:

```bash
mvn -s .github/settings.xml clean install
```
