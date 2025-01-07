package core.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import core.annotations.UseAuthentication
import core.authentication.JwtValidator
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.HttpHandlerExtensions
import java.lang.reflect.Method

class AuthenticationHandlerFilter(private val method: Method) : Filter(), HttpHandlerExtensions {
    override fun doFilter(exchange: HttpExchange?, chain: Chain?) {
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

            val validToken: Boolean = JwtValidator("SECRET_KEY").verifyToken(token)

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