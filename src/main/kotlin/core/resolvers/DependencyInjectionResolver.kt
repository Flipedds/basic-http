package core.resolvers

import core.di.DIContainer.getBean
import java.lang.reflect.Constructor
import kotlin.jvm.Throws
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
class DependencyInjectionResolver<T>(
    private val clazz: Class<*>) {

    @Throws(ClassNotFoundException::class)
    fun getInstance(): T {
        val constructor: Constructor<*> = clazz.constructors.first()
        val injectableConstructorParameters: List<KParameter> = constructor.kotlinFunction!!.parameters
        val parameterTypes: Array<Class<*>> = constructor.parameterTypes
        val dependencies: Array<Any?> = arrayOfNulls(parameterTypes.size)

        if (injectableConstructorParameters.isEmpty()) {
            return constructor.newInstance() as T
        }

        parameterTypes.indices.forEach { i -> dependencies[i] = getBean(parameterTypes[i]) }

        /**
         * Return an instance of <T> with injected parameters
         */
        return constructor.newInstance(*dependencies) as T
    }
}