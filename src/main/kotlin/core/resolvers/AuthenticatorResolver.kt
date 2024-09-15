package core.resolvers

import com.sun.net.httpserver.Authenticator
import core.annotations.Mapping
import core.authentication.ServerDefaultBasicAuthentication
import core.config.HttpContextConfig
import core.enums.LogColors
import core.logs.BasicLog
import java.lang.reflect.Constructor
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

object AuthenticatorResolver {
    fun getAuthenticator(properties: Properties, mapping: Mapping): Authenticator? {
        var authenticator: Authenticator? = null
        try {
            val authClass: Class<*> = Class.forName(
                properties.getProperty("server.auth")
                    ?: "core.authentication.ServerDefaultBasicAuthentication"
            )
            val authClassConstructor: Constructor<*> = authClass.constructors.first()
            val parameters: List<KParameter> = authClassConstructor.kotlinFunction!!.parameters
            authenticator =
                authClassConstructor.kotlinFunction?.callBy(mapOf(parameters[0] to mapping.path)) as Authenticator?

            if (authenticator is ServerDefaultBasicAuthentication) {
                BasicLog.getLogWithColorFor<HttpContextConfig>(
                    LogColors.RED, "Using default server Authenticator"
                )
            }
        } catch (exception: ClassNotFoundException) {
            BasicLog.getLogWithColorFor<HttpContextConfig>(
                LogColors.RED, "Auth class not found: ${exception.message}"
            )
        }
        return authenticator
    }
}