package core.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.annotations.Body
import core.annotations.Mapping
import core.annotations.QueryParam
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController
import core.interfaces.HttpHandlerExtensions
import java.io.IOException
import java.lang.reflect.Method
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
class RequestHttpHandler(
    private val resource: BaseController,
    private val method: Method) : HttpHandler, HttpHandlerExtensions {
    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        val mapping = method.getAnnotation(Mapping::class.java)
        if (exchange.requestURI.path != mapping.path) {
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

        val methodParameters = method.kotlinFunction?.parameters
        val listOfParameters = mutableListOf<Any?>()

        methodParameters?.forEach foreach@{ parameter ->
            if (parameter.name == "null") {
                listOfParameters.add(null)
                return@foreach
            }
            if (parameter.hasAnnotation<QueryParam>()) {
                listOfParameters.add(
                    exchange.requestURI.query?.toMapIfQuery()?.get(parameter.findAnnotation<QueryParam>()!!.key)
                )
                return@foreach
            }

            if (parameter.hasAnnotation<Body>()) {
                listOfParameters.add(
                    String(
                        exchange
                            .requestBody
                            .readAllBytes(),
                        StandardCharsets.UTF_8
                    )
                        .jsonToObject(parameter.type.javaType.typeName)
                )
                return@foreach
            }
        }
        exchange.send(
            method.invoke(
                resource,
                *listOfParameters.toTypedArray()
            ) as Json<Any>
        )
    }
}