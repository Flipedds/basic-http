package core.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.annotations.*
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

/**
 * This class is responsible for handling the request and invoking the method of the controller
 * that corresponds to the request.
 *
 * @param resource: The controller that contains the method to be invoked.
 * @param method: The method to be invoked.
 * @param path: The path of the request.
 */
@Suppress("UNCHECKED_CAST")
class RequestHttpHandler(
    private val resource: BaseController,
    private val method: Method,
    private val path: String
) : HttpHandler, HttpHandlerExtensions {
    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        val methodParameters = method.kotlinFunction?.parameters
        val listOfParameters = mutableListOf<Any?>()

        methodParameters?.forEach foreach@{ parameter ->
            if (parameter.name == "null") {
                listOfParameters.add(null)
                return@foreach
            }
            if (parameter.hasAnnotation<QueryParam>()) {
                val queryParam = exchange.requestURI.query?.toMapIfQuery()?.get(parameter.findAnnotation<QueryParam>()!!.key)

                if(queryParam == null) {
                    exchange.send(
                        Json(
                            message = "Bad Request !! " + "Query parameter ${parameter.name} is required !!",
                            statusCode = StatusCode.BadRequest
                        )
                    )
                    return
                }

                val parsedQueryParam = queryParam parseTo parameter.type.javaType.typeName

                if(parsedQueryParam == null) {
                    exchange.send(
                        Json(
                            message = "Bad Request !! " + "Query parameter ${parameter.name} is not in the correct format !!",
                            statusCode = StatusCode.BadRequest
                        )
                    )
                    return
                }

                listOfParameters.add(
                    parsedQueryParam
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

            if(parameter.hasAnnotation<PathParam>()){
                val pathParam = exchange.requestURI.path.replace(path, "") parseTo parameter.type.javaType.typeName
                listOfParameters.add(pathParam)
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