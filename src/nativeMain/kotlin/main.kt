import example.initServer
import io.github.domgew.kedis.KedisClient
import io.github.domgew.kedis.KedisConfiguration
import io.github.domgew.kedis.KedisConfiguration.Authentication
import io.github.domgew.kedis.commands.KedisCommand
import io.github.domgew.kedis.commands.KedisValueCommands
import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.Statement
import io.github.smyrgeorge.sqlx4k.postgres.PostgreSQL
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.log
import io.ktor.server.cio.CIO
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime



fun main() {
    val server = initServer()
    server.start(wait = true)
}