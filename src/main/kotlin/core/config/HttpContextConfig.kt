package core.config

import com.sun.net.httpserver.Authenticator
import com.sun.net.httpserver.HttpServer
import core.annotations.Mapping
import core.handlers.RequestHandler
import core.interfaces.BaseController
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.Optional
import java.util.Properties
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

class HttpContextConfig(val properties: Properties, private val server: HttpServer) {
    private val authConstructorAndParameters: Pair<Constructor<*>, List<KParameter>>? =
        getAuthClassConstructorAndParameters(properties).getOrNull()

    fun createContexts(resource: BaseController) {
        resource.javaClass.methods.forEach loop@{ method: Method ->
            val mapping: Mapping = method.getAnnotation(Mapping::class.java) ?: return@loop

            if (!mapping.authentication) {
                server.createContext(mapping.path, RequestHandler(resource, method))
                println(
                    "path: ${mapping.path} " +
                            "-> Method: ${mapping.method.name} " +
                            " -> Authentication: false"
                )
                return@loop
            }
            server.createContext(mapping.path, RequestHandler(resource, method)).setAuthenticator(
                authConstructorAndParameters?.first?.kotlinFunction?.callBy(
                    mapOf(
                        authConstructorAndParameters.second[0] to mapping.path
                    )
                ) as Authenticator?
            )
            println(
                "path: ${mapping.path} " +
                        "-> Method: ${mapping.method.name} " +
                        " -> Authentication: ${authConstructorAndParameters != null}"
            )
        }
    }

    private fun getAuthClassConstructorAndParameters(properties: Properties): Optional<Pair<Constructor<*>, List<KParameter>>> {
        var pairOfAuthConstructorAndParameters: Pair<Constructor<*>, List<KParameter>>? = null
        try {
            val authClass: Class<*> = Class.forName(properties.getProperty("server.auth") ?: "default")
            val authClassConstructor: Constructor<*> = authClass.constructors.first()
            val parameters: List<KParameter> = authClassConstructor.kotlinFunction!!.parameters
            pairOfAuthConstructorAndParameters = authClassConstructor to parameters
        } catch (exception: ClassNotFoundException) {
            println("Auth class not found: ${exception.message}")
        }
        return Optional.ofNullable(pairOfAuthConstructorAndParameters)
    }
}