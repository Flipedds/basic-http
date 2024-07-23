package core.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.annotations.Mapping
import core.domain.Json
import core.enums.StatusCode
import java.io.IOException
import java.lang.reflect.Method
import core.helpers.Helpers.Companion.queryToMap
import core.interfaces.BaseController
import core.responses.HttpResponse
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
            HttpResponse.send(
                exchange = exchange,
                json = Json(
                    message = "Resource Not Found !!", code = StatusCode.NotFound.code
                )
            )
            return
        }

        if (exchange.requestMethod != mapping.method.toString()) {
            HttpResponse.send(
                exchange = exchange,
                json = Json(
                    message = "Method not allowed !!", code = StatusCode.NotAllowed.code
                )
            )
            return
        }
        if (mapping.queryParams && exchange.requestURI.query == null) {
            HttpResponse.send(
                exchange = exchange,
                json = Json(
                    message = "Bad Request !! " + "This resource needs at least one query parameter !!",
                    code = StatusCode.NotAllowed.code
                )
            )
            return
        }

        if (mapping.queryParams && exchange.requestURI.query.isNotEmpty()) {
            method.invoke(resource, exchange, queryToMap(exchange.requestURI.query))
            return
        }
        method.invoke(resource, exchange)
    }
}