package example

import io.github.domgew.kedis.KedisClient
import io.github.domgew.kedis.KedisConfiguration
import io.github.domgew.kedis.KedisConfiguration.Authentication
import io.github.domgew.kedis.commands.KedisValueCommands
import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.Statement
import io.github.smyrgeorge.sqlx4k.postgres.PostgreSQL
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.log
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

fun someFunction() {
    throw NotImplementedError()
}

class TestClass {
    val log: Logger = KtorSimpleLogger("TestClass")
}

fun Application.installHealthCheckModule() {
    routing {
        get("/health") {
            call.respondText("ok")
        }
    }
}

@OptIn(ExperimentalTime::class)
fun initServer(): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
    println("begin main()")
    val kedisClient = KedisClient(
        configuration = KedisConfiguration(
            endpoint = KedisConfiguration.Endpoint.HostPort("localhost"),
            authentication = Authentication.NoAutoAuth,
            connectionTimeout = 200.milliseconds,
        ),
    )

    val db = PostgreSQL(
        url = "postgresql://localhost:5432/app",
        username = "app",
        password = "app",
        options = ConnectionPool.Options.builder().apply {
            maxConnections(10)
            minConnections(3)
        }.build()
    )

    val beginProgram = Clock.System.now()
    val server = embeddedServer(
        factory = CIO,
        //        configure = {
        //            // 多端口配置方式
        //            connectors.add(EngineConnectorBuilder().apply {
        //                port = 8080
        //            })
        //            connectors.add(EngineConnectorBuilder().apply {
        //                port = 8081
        //            })
        //        },
        //        environment = applicationEnvironment {
        //            log = KtorSimpleLogger("ktor-default")
        //        }
        port = 8080,
    ) {
        environment.config
        installHealthCheckModule()
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

            get("/count") {
                val count = kedisClient.execute(KedisValueCommands.incr("test_count"))
                call.respondText("count: $count", ContentType.Text.Html)
            }

            get("/customer") {
                val statement = Statement
                    .create("select * from customer where id = :id")
                    .bind("id", 1)
                val a = db.fetchAll(statement).getOrNull()
                val nick = a?.single()?.get("nick")?.asString()
                call.respondText("customer: $nick", ContentType.Text.Html)
            }
        }
    }.apply {
        // [INFO] (io.ktor.server.Application): Application started in 0.002 seconds. // 会默认输出
        monitor.subscribe(ApplicationStarted) { app ->
            val readyForServe = Clock.System.now()
            val costTime = readyForServe - beginProgram
            app.log.info("prepare cost time: $costTime")
        }
    }

    return server
}