package core.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.annotations.Mapping
import core.domain.Json
import core.extensions.send
import core.enums.StatusCode
import core.extensions.toMapIfQuery
import java.io.IOException
import java.lang.reflect.Method
import core.interfaces.BaseController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Suppress("UNCHECKED_CAST")
class RequestHandler(private val resource: BaseController, private val method: Method) : HttpHandler {
    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        println(
            "Request to -> ${exchange.requestURI.path} with method: ${exchange.requestMethod} at ${
                LocalTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss")
                )
            }"
        )

        val mapping = method.getAnnotation(Mapping::class.java)
        if (exchange.requestURI.path != mapping.path) {
            exchange.send(
                Json(
                    message = "Resource Not Found !!",
                    code = StatusCode.NotFound.code
                )
            )
            return
        }

        if (exchange.requestMethod != mapping.method.toString()) {
            exchange.send(
                Json(
                    message = "Method not allowed !!",
                    code = StatusCode.NotAllowed.code
                )
            )
            return
        }
        if (mapping.queryParams && exchange.requestURI.query == null) {
            exchange.send(
                Json(
                    message = "Bad Request !! " + "This resource needs at least one query parameter !!",
                    code = StatusCode.NotAllowed.code
                )
            )
            return
        }

        if (mapping.queryParams && exchange.requestURI.query.isNotEmpty()) {
            exchange.send(method.invoke(resource, exchange.requestURI.query.toMapIfQuery()["id"]) as Json<Any>)
            return
        }
        exchange.send(method.invoke(resource) as Json<Any>)
    }
}