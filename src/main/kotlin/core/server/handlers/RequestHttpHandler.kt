package core.server.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.domain.controller.BaseController
import core.domain.request.Body
import core.domain.request.ParameterData
import core.domain.request.PathParam
import core.domain.request.QueryParam
import core.domain.response.Json
import core.domain.response.StatusCode
import core.server.extensions.HttpHandlerExtensions
import java.io.IOException
import java.lang.reflect.Method
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

/**
 * This class is responsible for handling the request and invoking the method of the controller that
 * corresponds to the request.
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

                methodParameters
                        ?.map {
                                ParameterData(
                                        name = it.name,
                                        typeName = it.type.javaType.typeName,
                                        parameter = it,
                                        nameIsNull = it.name == "null",
                                        hasQuery = it.hasAnnotation<QueryParam>(),
                                        hasBody = it.hasAnnotation<Body>(),
                                        hasPathParam = it.hasAnnotation<PathParam>()
                                )
                        }
                        ?.forEach foreach@{ parameterData ->
                                if (parameterData.nameIsNull) {
                                        listOfParameters.add(null)
                                        return@foreach
                                }
                                if (parameterData.hasQuery) {
                                        val queryParam =
                                                exchange.requestURI
                                                        .query
                                                        ?.toMapIfQuery()
                                                        ?.get(parameterData.parameter.findAnnotation<QueryParam>()!!.key)

                                        if (queryParam == null) {
                                                exchange.send(
                                                        Json(
                                                                message =
                                                                        "Bad Request !! " +
                                                                                "Query parameter ${parameterData.name} is required !!",
                                                                statusCode = StatusCode.BadRequest
                                                        )
                                                )
                                                return
                                        }

                                        val parsedQueryParam =
                                                queryParam parseTo parameterData.typeName

                                        if (parsedQueryParam == null) {
                                                exchange.send(
                                                        Json(
                                                                message =
                                                                        "Bad Request !! " +
                                                                                "Query parameter ${parameterData.name} is not in the correct format !!",
                                                                statusCode = StatusCode.BadRequest
                                                        )
                                                )
                                                return
                                        }

                                        listOfParameters.add(parsedQueryParam)
                                        return@foreach
                                }

                                if (parameterData.hasBody) {
                                        listOfParameters.add(
                                                String(
                                                                exchange.requestBody.readAllBytes(),
                                                                StandardCharsets.UTF_8
                                                        )
                                                        .jsonToObject(parameterData.typeName)
                                        )
                                        return@foreach
                                }

                                if (parameterData.hasPathParam) {
                                        val pathParam =
                                                exchange.requestURI.path.replace(path, "") parseTo
                                                        parameterData.typeName
                                        listOfParameters.add(pathParam)
                                        return@foreach
                                }
                        }
                exchange.send(method.invoke(resource, *listOfParameters.toTypedArray()) as Json<Any>)
        }
}
