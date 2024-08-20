package core.resolvers

import core.di.DIContainer
import java.lang.reflect.Constructor
import kotlin.jvm.Throws
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
class DependencyInjectionResolver<T>(private val className: String) {

    @Throws(ClassNotFoundException::class)
    fun getInstance(): T {
        val injectableClass: Class<*> = Class.forName(className)
        val injectableConstructor: Constructor<*> = injectableClass.constructors.first()
        val injectableConstructorParameters: List<KParameter> = injectableConstructor.kotlinFunction!!.parameters

        if (injectableConstructorParameters.isEmpty()) {
            return injectableConstructor.newInstance() as T
        }
        /**
         * Return an instance of <T> with injected parameters
         */
        return injectableConstructor.kotlinFunction!!.callBy(injectableConstructorParameters.associate { kParameter ->
            return@associate injectableConstructorParameters[kParameter.index] to DIContainer.getBean(
                Class.forName(kParameter.type.javaType.typeName))
        }) as T
    }
}