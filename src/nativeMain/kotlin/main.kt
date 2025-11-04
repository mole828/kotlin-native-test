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
    val server = embeddedServer(
        factory = CIO,
        configure = {
            // 多端口配置方式
            connectors.add(EngineConnectorBuilder().apply {
                port = 8080
            })
            connectors.add(EngineConnectorBuilder().apply {
                port = 8081
            })
        },
        environment = applicationEnvironment {
            log = KtorSimpleLogger("ktor-default")
        }
//        port = 8080,
    ) {
        environment.config
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
//        // [INFO] (io.ktor.server.Application): Application started in 0.002 seconds. // 会默认输出
//        monitor.subscribe(ApplicationStarted) { app ->
//            val readyForServe = Clock.System.now()
//            val costTime = readyForServe - beginProgram
//            app.log.info("prepare cost time: $costTime")
//        }
        environment.log
    }
//    // start() 默认添加了
//    server.addShutdownHook {
//        server.stop()
//    }
    server.start(wait = true)
}