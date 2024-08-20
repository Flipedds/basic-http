package core.di

import core.annotations.Injectable
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import java.lang.reflect.Constructor
import kotlin.jvm.Throws

@Suppress("UNCHECKED_CAST")
object DIContainer {
    private val beanMap: MutableMap<Class<*>, Any> = HashMap()

    fun initializeDI() {
        beanMap.clear()
        ClassGraph()
            .enableAllInfo()
            .acceptPackages()
            .scan()
            .use { scanResult: ScanResult ->
                scanResult
                    .getClassesWithAnyAnnotation(Injectable::class.java.name)
                    .forEach { classWithInjectable: ClassInfo ->
                        createBean(
                            classWithInjectable.loadClass()
                        )
                    }
            }
        println(" <<<< ------ BEANS ---- >>>> ")
        beanMap.forEach { (clazz, any) -> println("${clazz.simpleName} -> $any") }
        println(" <<<< ----------------- >>>> ")
    }

    @Throws(NotImplementedError::class)
    private fun <T> createBean(clazz: Class<*>): T {
        var toBeInjectedClass = clazz
        val keyForBeanClass = toBeInjectedClass

        if (toBeInjectedClass.isInterface) {
            toBeInjectedClass = Class.forName(
                ClassGraph()
                    .enableAllInfo()
                    .acceptPackages()
                    .scan()
                    .let { scanResult: ScanResult ->
                        scanResult
                            .getClassesImplementing(keyForBeanClass.name)
                            .firstOrNull()?.name
                            ?: throw NotImplementedError("Interface: ${keyForBeanClass.simpleName} not implemented")
                    })
        }

        val constructor: Constructor<*> = toBeInjectedClass.constructors.first()
        val parameterTypes: Array<Class<*>> = constructor.parameterTypes
        val dependencies = arrayOfNulls<Any>(parameterTypes.size)

        for (i in parameterTypes.indices) {
            dependencies[i] = getBean(parameterTypes[i])
        }

        val bean = constructor.newInstance(*dependencies)
        beanMap[keyForBeanClass] = bean
        return bean as T
    }

    fun <T : Any> getBean(clazz: Class<*>): T {
        if (beanMap.containsKey(clazz)) {
            return beanMap[clazz] as T
        }
        return createBean(clazz) as T
    }
}