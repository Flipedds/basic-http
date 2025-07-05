package core.di

import common.LogColors
import common.BasicLog
import core.domain.di.Injectable
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import java.lang.reflect.Constructor
import kotlin.jvm.Throws

/**
 * This class is responsible for creating and managing dependency injection.
 * It uses reflection to create beans and inject them into the classes that need them.
 * It scans the project for classes annotated with @Injectable and creates beans for them.
 * It also scans for classes that implement interfaces and creates beans for them.
 * It stores the beans in a map and provides them when needed.
 * It also logs the beans created.
 */
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
                        getBean(
                            classWithInjectable.loadClass()
                        )
                    }
            }

        BasicLog.getLogWithColorFor<DIContainer>(
            LogColors.BLUE, " <<<< ------ BEANS ---- >>>> ")

        beanMap.forEach { (clazz, any) ->
            BasicLog.getLogWithColorFor<DIContainer>(
            LogColors.BLUE, "${clazz.simpleName} -> $any") }

        BasicLog.getLogWithColorFor<DIContainer>(
            LogColors.BLUE," <<<< ----------------- >>>> ")
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

        parameterTypes.indices.forEach { i -> dependencies[i] = getBean(parameterTypes[i]) }

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