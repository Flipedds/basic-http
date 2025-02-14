package core.config

import com.sun.net.httpserver.HttpServer
import core.annotations.Mapping
import core.annotations.PathParam
import core.enums.LogColors
import core.filters.AuthenticationHandlerFilter
import core.filters.PreRequestHandlerLogFilter
import core.filters.RequestValidationHandlerFilter
import core.handlers.RequestHttpHandler
import core.interfaces.BaseController
import core.logs.BasicLog
import java.lang.reflect.Method
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

/**
 * This class is responsible for creating the contexts for the HttpServer
 * and adding the filters to the contexts.
 * @param server: HttpServer - The server to create the contexts for.
 */
class HttpContextConfig(private val server: HttpServer) {
    fun createContexts(resource: BaseController) {
        resource.javaClass.methods.forEach loop@{ method: Method ->
            val mapping: Mapping = method.getAnnotation(Mapping::class.java) ?: return@loop
            val methodHasPathParam = method.kotlinFunction!!.parameters.any { it.hasAnnotation<PathParam>() }
            val regex = "\\{(\\w+)}".toRegex()
            val path = if(methodHasPathParam) mapping.path.replace(regex, "") else mapping.path

                server.createContext(
                    path,
                    RequestHttpHandler(resource, method, path))
                    .apply {
                        filters.add(PreRequestHandlerLogFilter())
                        filters.add(AuthenticationHandlerFilter(method))
                        filters.add(RequestValidationHandlerFilter(method, methodHasPathParam))
                    }.let {
                        BasicLog.getLogWithColorFor<HttpContextConfig>(
                            LogColors.YELLOW,
                            "path: ${mapping.path} " +
                                    "-> Method: ${mapping.method.name} "
                        )
                    }
                return@loop
        }
    }
}