package core.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.annotations.Mapping
import core.catchers.ResponseCatchers.Companion.badRequest
import core.catchers.ResponseCatchers.Companion.methodNotAllowed
import core.catchers.ResponseCatchers.Companion.resourceNotFound
import java.io.IOException
import java.lang.reflect.Method
import core.helpers.Helpers.Companion.queryToMap
import core.interfaces.BaseController
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
        if(exchange.requestURI.path != mapping.path){
            resourceNotFound(exchange)
            return
        }

        if (exchange.requestMethod != mapping.method.toString()) {
            methodNotAllowed(exchange)
            return
        }
        if (mapping.queryParams && exchange.requestURI.query == null) {
            badRequest(exchange, "This resource needs at least one query parameter")
            return
        }

        if (mapping.queryParams && exchange.requestURI.query.isNotEmpty()) {
            method.invoke(resource, exchange, queryToMap(exchange.requestURI.query))
            return
        }
        method.invoke(resource, exchange)
    }
}