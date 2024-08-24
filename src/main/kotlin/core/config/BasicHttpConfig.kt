package core.config

import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.spi.HttpServerProvider
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

class BasicHttpConfig {
    companion object {
        private lateinit var server: HttpServer
        fun startServer() {
            val props = Properties(); props.load(FileInputStream("src/main/resources/server.properties"))
            val hostname = props.getProperty("server.host") ?: "localhost"
            val port = if (props.getProperty("server.port") != null) props.getProperty("server.port").toInt() else 8000
            val address = InetSocketAddress(hostname, port)
            server = HttpServerProvider.provider().createHttpServer(address, 0).apply {
                ControllersDependencyInjectionConfig(properties = props, server = this).withReflection()
                executor = Executors.newCachedThreadPool()
                println("Running server on ${getAddress().hostString} in port ${getAddress().port}")
                start()
            }
        }

        fun stopServer() {
            if (::server.isInitialized) {
                server.stop(0)
                println("Server stopped")
            }
        }
    }
}
