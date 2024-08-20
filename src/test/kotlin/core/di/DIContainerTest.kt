package core.di

import core.di.injectables.TestClass
import org.junit.jupiter.api.Test

class DIContainerTest {
    @Test
    fun `should initialize a di container and get a bean for TestClass`() {
        DIContainer.initializeDI()
        val teste: TestClass = DIContainer.getBean(Class.forName("core.di.injectables.TestClass"))
        teste.testMethod()
    }
}