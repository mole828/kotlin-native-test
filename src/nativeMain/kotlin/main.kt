import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.log
import io.ktor.server.cio.CIO
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun someFunction() {
    throw NotImplementedError()
}

class TestClass {
    val log: Logger = KtorSimpleLogger("TestClass")
}

@OptIn(ExperimentalTime::class)
fun main() {
    val beginProgram = Clock.System.now()
    val server = embeddedServer(CIO, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                TestClass().log.info("c!")
                log.info("hello, world!")
                call.respondText("Hello, world!", ContentType.Text.Html)
            }

            get("/test/logger") {
                call.respondText("log::class: ${log::class}")
            }

            get("/test/error") {
                someFunction()
                call.respondText("ok", ContentType.Text.Html)
            }
        }
    }.apply {
        monitor.subscribe(ApplicationStarted) { app ->
            val readyForServe = Clock.System.now()
            val costTime = readyForServe - beginProgram
            app.log.info("prepare cost time: $costTime")
        }
    }
    server.addShutdownHook {
        server.stop()
    }
    server.start(wait = true)
}