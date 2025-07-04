package core.resolvers

import api_for_test.controllers.UserController
import api_for_test.entities.User
import core.domain.authentication.JwtValidator
import core.di.DIContainer
import core.di.DependencyInjectionResolver
import core.domain.response.Json
import core.domain.response.StatusCode
import core.domain.controller.BaseController
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import shared.BaseTest

class DependencyInjectionResolverTest: BaseTest() {
    @Test
    fun `should get a instance of a UserController and return correct authUser Json Object`() {
        val instance: UserController = DependencyInjectionResolver<BaseController>(
            Class.forName("api_for_test.controllers.UserController"))
            .getInstance() as UserController

        val authUser = instance.authUser()

        authUser.message eq "User authenticated !!"
        authUser.code eq 200
        JwtValidator("SECRET_KEY").verifyToken(authUser.data as String) eq true
    }

    @Test
    fun `should get a instance of a UserController and return correct getUser Json Object`() {
        val instance: UserController = DependencyInjectionResolver<BaseController>(
            Class.forName("api_for_test.controllers.UserController"))
            .getInstance() as UserController

        val user = User(id=1, name="teste")

        val getUser = Json(message="User found !!", code=200, data= user)

        getUser eq instance.getUser(1)
    }

    @Test
    fun `should get a instance of a UserController and return correct getUserById Json Object`() {
        val instance: UserController = DependencyInjectionResolver<BaseController>(
            Class.forName("api_for_test.controllers.UserController"))
            .getInstance() as UserController

        val user = User(id=1, name="teste")

        val getUser = Json(message="User found !!", code=200, data= user)

        getUser eq instance.getUserById(1)
    }

    @Test
    fun `should get a instance of a UserController and return correct postUser Json Object`() {
        val instance: UserController = DependencyInjectionResolver<BaseController>(
            Class.forName("api_for_test.controllers.UserController"))
            .getInstance() as UserController

        val user = User(id=1, name="teste")

        val postUser = Json(
            message = "User created",
            statusCode = StatusCode.Created,
            data = user)

        postUser eq instance.postUser(user)
    }

    @Test
    fun `should get a instance of a UserController and return correct getUserList Json Object`() {
        val instance: UserController = DependencyInjectionResolver<BaseController>(
            Class.forName("api_for_test.controllers.UserController"))
            .getInstance() as UserController

        val getUserList = Json(
            message = "Users found !!",
            statusCode = StatusCode.Ok,
            data = listOf(
                User(1, "teste"),
                User(2, "teste"),
                User(3, "teste")
            ))
        getUserList eq instance.getUserList()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(){
            DIContainer.initializeDI()
        }
    }
}