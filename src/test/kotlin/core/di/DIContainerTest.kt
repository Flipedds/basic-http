package core.di

import core.di.injectables.TestClass
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DIContainerTest {
    @Test
    fun `should initialize a di container and get a bean for TestClass`() {
        DIContainer.initializeDI()
        val teste: TestClass = DIContainer.getBean(Class.forName("core.di.injectables.TestClass"))
        assertEquals("testMethod", teste.testMethod())
    }
}