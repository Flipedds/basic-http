package com.example.controllers

import com.example.dtos.TbTestDto
import com.example.entities.User
import com.example.interfaces.ITbTestService
import com.example.interfaces.IUserService
import core.annotations.Body
import core.annotations.Controller
import core.annotations.Mapping
import core.annotations.PathParam
import core.annotations.QueryParam
import core.annotations.UseAuthentication
import core.authentication.JwtCreator
import core.domain.Json
import core.enums.RequestMethod
import core.enums.StatusCode
import core.interfaces.BaseController
import core.interfaces.IJwtCreator

@Controller
class UserController(private val userService: IUserService,
    private val tbTestService: ITbTestService) : BaseController,
    IJwtCreator by JwtCreator("SECRET_KEY"){

    @Mapping(path = "/users/auth", method = RequestMethod.POST)
    fun authUser(): Json<String> {
        return Json(
            message = "User authenticated !!",
            statusCode = StatusCode.Ok,
            data = createJwt("user")
        )
    }

    @Mapping(path = "/test/get/{id}", method = RequestMethod.GET)
    fun getTestById(@PathParam id: Int?): Json<TbTestDto> {
        return Json(
            message = "Test found !!",
            statusCode = StatusCode.Ok,
            data = id?.let { tbTestService.getTbTestById(it) }
        )
    }


    @UseAuthentication
    @Mapping(path = "/users/get/{id}", method = RequestMethod.GET)
    fun getUserById(@PathParam id: Int?): Json<User> {
        return Json(
            message = "User found !!",
            statusCode = StatusCode.Ok,
            data = id?.let { userService.getUserById(it) }
        )
    }

    @UseAuthentication
    @Mapping(path = "/users", method = RequestMethod.GET)
    fun getUser(@QueryParam("id") id: Int?): Json<User> {
        return Json(
            message = "User found !!",
            statusCode = StatusCode.Ok,
            data = id?.let { userService.getUserById(it) }
        )
    }

    @Mapping(path = "/users/create", method = RequestMethod.POST)
    fun postUser(@Body user: User): Json<User> {
        println(user.id)
        println(user.name)
        return Json(
            message = "User created",
            statusCode = StatusCode.Created,
            data = user
        )
    }

    @Mapping(path = "/users/", method = RequestMethod.GET)
    fun getUserList(): Json<List<User>> {
        return Json(
            message = "Users found !!",
            statusCode = StatusCode.Ok,
            data = listOf(
                User(1, "teste"), User(2, "teste"), User(3, "teste")
            )
        )
    }
}