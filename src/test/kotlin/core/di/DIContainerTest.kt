package core.di

import api_for_test.entities.User
import api_for_test.interfaces.IUserRepository
import api_for_test.interfaces.IUserService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import shared.BaseTest

class DIContainerTest: BaseTest() {
    @Test
    fun `should get a bean for IUserRepository and return correct User object`() {
        val teste: IUserRepository = DIContainer.getBean(
            Class.forName("api_for_test.interfaces.IUserRepository"))
        User(id = 1, name = "teste") eq teste.getUserById(1)
    }

    @Test
    fun `should get a bean for IUserService and return correct User object`() {
        val teste: IUserService = DIContainer.getBean(
            Class.forName("api_for_test.interfaces.IUserService"))
        User(id = 1, name = "teste") eq teste.getUserById(1)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(){
            DIContainer.initializeDI()
        }
    }
}