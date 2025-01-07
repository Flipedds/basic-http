package core.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import core.annotations.Mapping
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.HttpHandlerExtensions
import java.lang.reflect.Method

class RequestValidationHandlerFilter(
    private val method: Method,
    private val methodHasPathParam: Boolean
) : Filter(), HttpHandlerExtensions {
    override fun doFilter(exchange: HttpExchange?, chain: Chain?) {

        val mapping = method.getAnnotation(Mapping::class.java)

        if (exchange!!.requestURI.path != mapping.path && !methodHasPathParam) {
            exchange.send(
                Json(
                    message = "Resource Not Found !!",
                    statusCode = StatusCode.NotFound,
                )
            )
            return
        }

        if (exchange.requestMethod != mapping.method.toString()) {
            exchange.send(
                Json(
                    message = "Method not allowed !!",
                    statusCode = StatusCode.NotAllowed
                )
            )
            return
        }
        chain?.doFilter(exchange)
    }

    override fun description(): String {
        return "Validation Filter"
    }
}