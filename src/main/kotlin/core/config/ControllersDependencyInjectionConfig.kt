package core.config

import com.sun.net.httpserver.HttpServer
import core.annotations.Controller
import core.interfaces.BaseController
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import java.lang.reflect.Constructor
import java.util.Properties
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

class ControllersDependencyInjectionConfig(
    private val properties: Properties, private val server: HttpServer
) {
    fun withReflection(){
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
                    val resource =
                        resourceClass.constructors.first().kotlinFunction!!.callBy(resourceConstructorParameters.associate { classParameter: KParameter ->
                            resourceConstructorParameters[classParameter.index] to Class.forName(classParameter.type.javaType.typeName).constructors.first().kotlinFunction!!.callBy(
                                Class.forName(classParameter.type.javaType.typeName).constructors.first().kotlinFunction!!.parameters.associate { injectedClassParam: KParameter ->
                                    Class.forName(classParameter.type.javaType.typeName).constructors.first().kotlinFunction!!.parameters[injectedClassParam.index] to Class.forName(
                                        injectedClassParam.type.javaType.typeName
                                    ).getDeclaredConstructor().newInstance()
                                })

                        })
                    HttpContextConfig(
                        properties = properties, server = server
                    ).createContexts(resource as BaseController)
                }
        }
    }
}