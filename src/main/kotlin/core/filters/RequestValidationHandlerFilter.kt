package core.filters

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange
import core.annotations.Mapping
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.HttpHandlerExtensions
import java.lang.reflect.Method

/**
 * RequestValidationHandlerFilter class is a filter class that validates the request
 * based on the method and path specified in the Mapping annotation.
 * If the request method or path does not match the Mapping annotation, then it sends
 * the appropriate response to the client.
 *
 * @property method: Method of the controller.
 * @property methodHasPathParam: Boolean value that indicates if the method has a path parameter.
 * @constructor Creates a RequestValidationHandlerFilter.
 */
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