package core.server.config

import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.spi.HttpServerProvider
import core.di.ControllersDependencyInjectionConfig
import common.LogColors
import common.BasicLog
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

/**
 * Basic Http Config to start and stop the server
 * @property server HttpServer Server instance to start and stop the server
 * @property DEFAULT_PORT Int Default port
 * @property DEFAULT_HOST String Default host
 * @property props Properties instance
 */
object BasicHttpConfig {
    private lateinit var server: HttpServer
    private const val DEFAULT_PORT: Int = 3000
    private const val DEFAULT_HOST: String = "localhost"
    private val props: Properties = Properties()

    init {
        runCatching {
            FileInputStream("src/main/resources/server.properties")
        }.onSuccess { props.load(it) }
            .onFailure {
                BasicLog.getLogWithColorFor<BasicHttpConfig>(
                    LogColors.YELLOW,
                    StringBuilder()
                        .append("Using Server Default Properties !\n")
                        .append("| Add server.properties on main properties folder |\n")
                        .append("| Options:\n")
                        .append("| server.host -> example: localhost\n")
                        .append("| server.port -> example: 3000\n")
                        .toString()
                )
            }
    }

    fun startServer() {
        val hostname = props.getProperty("server.host") ?: DEFAULT_HOST
        val port = props.getProperty("server.port")?.toIntOrNull() ?: DEFAULT_PORT
        val address = InetSocketAddress(hostname, port)
        server = HttpServerProvider.provider().createHttpServer(address, 0).apply {
            ControllersDependencyInjectionConfig(server = this).withReflection()
            executor = Executors.newCachedThreadPool()
            BasicLog.getLogWithColorFor<BasicHttpConfig>(
                LogColors.GREEN,
                "Running server on ${getAddress().hostString} in port ${getAddress().port}"
            )
            start()
        }
    }

    fun stopServer() {
        if (BasicHttpConfig::server.isInitialized) {
            server.stop(0)
            BasicLog.getLogWithColorFor<BasicHttpConfig>(
                LogColors.RED,
                "Server stopped"
            )
        }
    }
}
