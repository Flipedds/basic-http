package api_for_test.controllers

import core.enums.RequestMethod
import api_for_test.entities.User
import api_for_test.interfaces.IUserService
import core.annotations.*
import core.authentication.JwtCreator
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController


@Controller
class UserController(private val userService: IUserService) : BaseController {

    @Mapping(path = "/users/auth", method = RequestMethod.POST)
    fun authUser(): Json<String>{
        return Json(
            message = "User authenticated !!",
            statusCode = StatusCode.Ok,
            data = JwtCreator("SECRET_KEY").createJwt("user")
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