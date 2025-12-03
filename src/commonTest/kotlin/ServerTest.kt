import example.initServer
import example.installHealthCheckModule
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class ServerTest {
    @Test
    fun testSomeFunction() {
        val server = initServer()
        testApplication {
            application {
                installHealthCheckModule()
            }

            val resp = client.get("/health")
            println(resp.bodyAsText())
        }
    }
}