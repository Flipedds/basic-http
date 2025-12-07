package core.server.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import core.domain.request.Body
import core.domain.request.PathParam
import core.domain.request.QueryParam
import core.domain.response.Json
import core.domain.response.StatusCode
import core.domain.controller.BaseController
import core.server.extensions.HttpHandlerExtensions
import java.io.IOException
import java.lang.reflect.Method
import java.nio.charset.StandardCharsets
import kotlin.reflect.KParameter
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
    
    // Cache parameter metadata to avoid reflection on every request
    private data class ParameterMetadata(
        val name: String?,
        val typeName: String,
        val queryParamKey: String?,
        val isQueryParam: Boolean,
        val isBodyParam: Boolean,
        val isPathParam: Boolean
    )
    
    private val parametersMetadata: List<ParameterMetadata> = method.kotlinFunction?.parameters?.map { parameter ->
        ParameterMetadata(
            name = parameter.name,
            typeName = parameter.type.javaType.typeName,
            queryParamKey = parameter.findAnnotation<QueryParam>()?.key,
            isQueryParam = parameter.hasAnnotation<QueryParam>(),
            isBodyParam = parameter.hasAnnotation<Body>(),
            isPathParam = parameter.hasAnnotation<PathParam>()
        )
    } ?: emptyList()
    
    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        val listOfParameters = mutableListOf<Any?>()

        parametersMetadata.forEach { paramMetadata ->
            if (paramMetadata.name == "null") {
                listOfParameters.add(null)
                return@forEach
            }
            if (paramMetadata.isQueryParam) {
                val queryParam = exchange.requestURI.query?.toMapIfQuery()?.get(paramMetadata.queryParamKey!!)

                if(queryParam == null) {
                    exchange.send(
                        Json(
                            message = "Bad Request !! " + "Query parameter ${paramMetadata.name} is required !!",
                            statusCode = StatusCode.BadRequest
                        )
                    )
                    return
                }

                val parsedQueryParam = queryParam parseTo paramMetadata.typeName

                if(parsedQueryParam == null) {
                    exchange.send(
                        Json(
                            message = "Bad Request !! " + "Query parameter ${paramMetadata.name} is not in the correct format !!",
                            statusCode = StatusCode.BadRequest
                        )
                    )
                    return
                }

                listOfParameters.add(parsedQueryParam)
                return@forEach
            }

            if (paramMetadata.isBodyParam) {
                listOfParameters.add(
                    String(
                        exchange
                            .requestBody
                            .readAllBytes(),
                        StandardCharsets.UTF_8
                    )
                        .jsonToObject(paramMetadata.typeName)
                )
                return@forEach
            }

            if(paramMetadata.isPathParam){
                val pathParam = exchange.requestURI.path.replace(path, "") parseTo paramMetadata.typeName
                listOfParameters.add(pathParam)
                return@forEach
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