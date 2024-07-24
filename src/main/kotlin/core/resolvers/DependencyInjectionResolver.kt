package core.resolvers

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import java.lang.reflect.Constructor
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
class DependencyInjectionResolver<T>(private val className: String) {
    fun getInstance(): T {
        var injectableClass: Class<*> = Class.forName(className)

        if (injectableClass.isInterface) {
            injectableClass = Class.forName(
                ClassGraph().enableAllInfo().acceptPackages().scan().let { scanResult: ScanResult ->
                    scanResult.getClassesImplementing(className).firstOrNull()?.name
                        ?: throw RuntimeException("Interface $className not implemented.")
                })
        }

        val injectableConstructor: Constructor<*> = injectableClass.constructors.first()
        val injectableConstructorParameters: List<KParameter> = injectableConstructor.kotlinFunction!!.parameters

        if (injectableConstructorParameters.isEmpty()) {
            return injectableConstructor.newInstance() as T
        }
        /**
         * Return an instance of <T> with injected parameters
         */
        return injectableConstructor.kotlinFunction!!.callBy(injectableConstructorParameters.associate { kParameter ->
            return@associate injectableConstructorParameters[kParameter.index] to DependencyInjectionResolver<Any>(
                kParameter.type.javaType.typeName
            ).getInstance()
        }) as T
    }
}