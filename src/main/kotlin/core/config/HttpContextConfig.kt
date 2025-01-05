package core.config

import com.sun.net.httpserver.HttpServer
import core.annotations.Mapping
import core.annotations.PathParam
import core.annotations.UseAuthentication
import core.enums.LogColors
import core.filters.PreRequestHandlerLogFilter
import core.handlers.RequestHttpHandler
import core.interfaces.BaseController
import core.logs.BasicLog
import core.resolvers.AuthenticatorResolver
import java.lang.reflect.Method
import java.util.Properties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

class HttpContextConfig(val properties: Properties, private val server: HttpServer) {
    fun createContexts(resource: BaseController) {
        resource.javaClass.methods.forEach loop@{ method: Method ->
            val mapping: Mapping = method.getAnnotation(Mapping::class.java) ?: return@loop
            val useAuthentication: UseAuthentication? = method.getAnnotation(UseAuthentication::class.java)
            val methodHasPathParam = method.kotlinFunction!!.parameters.any { it.hasAnnotation<PathParam>() }
            val regex = "\\{(\\w+)}".toRegex()
            val path = if(methodHasPathParam) mapping.path.replace(regex, "") else mapping.path

                server.createContext(
                    path,
                    RequestHttpHandler(resource, method, methodHasPathParam, path))
                    .apply {
                        filters.add(PreRequestHandlerLogFilter())
                        useAuthentication?.let {
                            this.setAuthenticator(AuthenticatorResolver.getAuthenticator(properties = properties, mapping = mapping))
                        }
                    }.let {
                        BasicLog.getLogWithColorFor<HttpContextConfig>(
                            LogColors.YELLOW,
                            "path: ${mapping.path} " +
                                    "-> Method: ${mapping.method.name} " +
                                    " -> Authentication: ${it.authenticator != null}"
                        )
                    }
                return@loop
        }
    }
}