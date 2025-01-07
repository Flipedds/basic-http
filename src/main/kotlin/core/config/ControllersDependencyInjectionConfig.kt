package core.config

import com.sun.net.httpserver.HttpServer
import core.annotations.Controller
import core.di.DIContainer
import core.interfaces.BaseController
import core.resolvers.DependencyInjectionResolver
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult

class ControllersDependencyInjectionConfig(
    private val server: HttpServer
) {
    fun withReflection() {
        /**
         * Scan all packages
         * To get info of classes with the @Controller annotation
         * And for each info, create object (new instance) with the parameters injected
         * And for each method in the class of the object create a http context to path and handler
         * Based in the properties of the annotation
         */
        DIContainer.initializeDI()
        ClassGraph().enableAllInfo().acceptPackages().scan().use { scanResult: ScanResult ->
            scanResult.getClassesWithAnyAnnotation(Controller::class.java.name)
                .forEach { classWithController: ClassInfo ->
                    val resource = DependencyInjectionResolver<BaseController>(
                        classWithController.loadClass()).getInstance()
                    HttpContextConfig(server = server).createContexts(resource)
                }
        }
    }
}