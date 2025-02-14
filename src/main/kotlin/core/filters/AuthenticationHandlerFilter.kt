package core.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import core.annotations.UseAuthentication
import core.authentication.JwtValidator
import core.config.BasicHttpConfig
import core.domain.Json
import core.enums.LogColors
import core.enums.StatusCode
import core.interfaces.HttpHandlerExtensions
import core.logs.BasicLog
import java.io.FileInputStream
import java.lang.reflect.Method
import java.util.*

/**
 * AuthenticationHandlerFilter
 * Filter to handle authentication
 * @param method: Method -> Method of the controller
 */
class AuthenticationHandlerFilter(private val method: Method) : Filter(), HttpHandlerExtensions {
    private val secretKey: String = "SECRET_KEY"
    private val props: Properties = Properties()

    init {
        runCatching {
            FileInputStream("src/main/resources/server.properties")
        }.onSuccess { props.load(it) }
            .onFailure {
                BasicLog.getLogWithColorFor<BasicHttpConfig>(
                    LogColors.YELLOW,
                    StringBuilder()
                        .append("Using Server Auth Default Properties !\n")
                        .append("| Add server.properties on main properties folder |\n")
                        .append("| Options:\n")
                        .append("| server.jwtkey -> example: SECRET_KEY\n")
                        .toString()
                )
            }
    }

    override fun doFilter(exchange: HttpExchange?, chain: Chain?) {
        val secretKey = props.getProperty("server.jwtkey") ?: secretKey

        val useAuthentication = method.getAnnotation(UseAuthentication::class.java)

        useAuthentication?.let {
            val authHeader = exchange!!.requestHeaders.getFirst("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.send(
                    Json(
                        message = "Unauthorized !!",
                        statusCode = StatusCode.Unauthorized
                    )
                )
                return
            }

            val token: String = authHeader.removePrefix("Bearer ")

            val validToken: Boolean = JwtValidator(secretKey).verifyToken(token)

            if (!validToken) {
                exchange.send(
                    Json(
                        message = "Unauthorized !!",
                        statusCode = StatusCode.Unauthorized
                    )
                )
                return
            }
        }
        chain?.doFilter(exchange)
    }

    override fun description(): String {
        return "Authentication Filter"
    }
}