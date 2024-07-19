package core.config

import com.sun.net.httpserver.Authenticator
import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.spi.HttpServerProvider
import core.annotations.Mapping
import core.handlers.RequestHandler
import core.annotations.Controller
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import java.io.FileInputStream
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

class BasicHttpConfig {
    companion object {
        fun startServer() {
            val props = Properties()
            props.load(FileInputStream("src/main/resources/server.properties"))
            val address = InetSocketAddress(
                props.getProperty("server.host"), props.getProperty("server.port").toInt()
            )
            val server: HttpServer = HttpServerProvider.provider().createHttpServer(address, 0)
            val authClass: Class<*> = Class.forName(props.getProperty("server.auth"))
            val authClassConstructor: Constructor<*> = authClass.constructors.first()
            val parameters: List<KParameter> = authClassConstructor.kotlinFunction!!.parameters
            /**
             * Scan all packages
             * To get info of classes with the @Controller annotation
             * And for each info, create object (new instance) with the parameters injected
             * And for each method in the class of the object create a http context to path and handler
             * Based in the properties of the annotation
             */
            ClassGraph().enableAllInfo().acceptPackages().scan().use { scanResult: ScanResult ->
                scanResult.getClassesWithAnyAnnotation(Controller::class.java.name)
                    .forEach { classWithController: ClassInfo ->
                        val resourceClass: Class<*> = Class.forName(classWithController.name)
                        val resourceConstructor: Constructor<*> = resourceClass.constructors.first()
                        val resourceConstructorParameters: List<KParameter> =
                            resourceConstructor.kotlinFunction!!.parameters
                        val resource = resourceClass.constructors.first().kotlinFunction!!.callBy(
                            resourceConstructorParameters.associate { classParameter: KParameter ->
                                resourceConstructorParameters[
                                    classParameter.index] to
                                        Class
                                            .forName(classParameter.type.javaType.typeName)
                                            .constructors.first().kotlinFunction!!.callBy(
                                                Class.forName(classParameter.type.javaType.typeName)
                                                    .constructors.first().kotlinFunction!!.parameters.associate { injectedClassParam: KParameter ->
                                                        Class.forName(classParameter.type.javaType.typeName)
                                                            .constructors.first().kotlinFunction!!.parameters[
                                                            injectedClassParam.index] to
                                                                Class
                                                                    .forName(injectedClassParam.type.javaType.typeName)
                                                                    .getDeclaredConstructor()
                                                                    .newInstance()
                                                    }
                                            )

                            }
                        )
                        resource.javaClass.methods.forEach loop@{ method: Method ->
                            val mapping: Mapping = method.getAnnotation(Mapping::class.java) ?: return@loop
                            println("path: ${mapping.path} -> Method: ${mapping.method.name} -> Query Params: ${mapping.queryParams} -> Authentication: ${mapping.authentication}")
                            if (!mapping.authentication) {
                                server.createContext(mapping.path, RequestHandler(resource, method))

                                return@loop
                            }
                            server.createContext(mapping.path, RequestHandler(resource, method)).setAuthenticator(
                                authClassConstructor.kotlinFunction?.callBy(mapOf(parameters[0] to mapping.path)) as Authenticator
                            )
                        }
                    }
            }
            with(server) {
                executor = null
                start()
            }
        }
    }
}
