package api.controllers

import core.annotations.Mapping
import core.annotations.Controller
import core.enums.RequestMethod
import api.entities.User
import api.interfaces.IUserService
import core.annotations.QueryParam
import core.annotations.UseAuthentication
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController


@Controller
class UserController(private val userService: IUserService) : BaseController {
    @UseAuthentication
    @Mapping(path = "/users", method = RequestMethod.GET)
    fun getUser(@QueryParam("id") id: String?): Json<User> {
        if (id == null) {
            return Json(
                message = "Bad Request !! " + "Query parameter id is required !!",
                code = StatusCode.BadRequest.code
            )
        }
        return Json(
            message = "User found !!",
            code = StatusCode.Ok.code,
            data = userService.getUserById(id.toInt())
        )
    }

    @Mapping(path = "/users/", method = RequestMethod.GET)
    fun getUserList(): Json<List<User>> {
        return Json(
            message = "Users found !!",
            code = StatusCode.Ok.code,
            data = listOf(
                User(1, "teste"), User(2, "teste"), User(3, "teste")
            )
        )
    }
}