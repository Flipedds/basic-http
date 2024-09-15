package core.config

import com.sun.net.httpserver.HttpServer
import core.annotations.Mapping
import core.annotations.UseAuthentication
import core.enums.LogColors
import core.filters.PreRequestHandlerLogFilter
import core.handlers.RequestHttpHandler
import core.interfaces.BaseController
import core.logs.BasicLog
import core.resolvers.AuthenticatorResolver
import java.lang.reflect.Method
import java.util.Properties

class HttpContextConfig(val properties: Properties, private val server: HttpServer) {
    fun createContexts(resource: BaseController) {
        resource.javaClass.methods.forEach loop@{ method: Method ->
            val mapping: Mapping = method.getAnnotation(Mapping::class.java) ?: return@loop
            val useAuthentication: UseAuthentication? = method.getAnnotation(UseAuthentication::class.java)
                server.createContext(mapping.path, RequestHttpHandler(resource, method))
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