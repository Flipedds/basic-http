package core.resolvers

import java.lang.reflect.Constructor
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
class DependencyInjectionResolver<T>(private val className: String) {
    fun getInstance(): T {
        val controllerClass: Class<*> = Class.forName(className)
        val controllerConstructor: Constructor<*> = controllerClass.constructors.first()
        val controllerConstructorParameters: List<KParameter> = controllerConstructor.kotlinFunction!!.parameters

        if (controllerConstructorParameters.isEmpty()) {
            return controllerConstructor.newInstance() as T
        }
        /**
         * Return an instance of <T> with injected parameters
         */
        return controllerConstructor.kotlinFunction!!.callBy(
            controllerConstructorParameters.associate { kParameter ->
                return@associate controllerConstructorParameters[kParameter.index] to
                        DependencyInjectionResolver<Any>(kParameter.type.javaType.typeName).getInstance()
            }
        ) as T
    }
}