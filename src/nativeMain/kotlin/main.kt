import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.log
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
    val beginProgram = Clock.System.now()
    embeddedServer(CIO, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                log.info("hello, world!")
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }.apply {
        monitor.subscribe(ApplicationStarted) { app ->
            val readyForServe = Clock.System.now()
            val costTime = readyForServe - beginProgram
            app.log.info("prepare cost time: $costTime")
        }
    }.start(wait = true)
}