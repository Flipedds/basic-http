package api.controllers

import com.sun.net.httpserver.HttpExchange
import core.annotations.Mapping
import core.annotations.Controller
import core.enums.RequestMethod
import api.entities.User
import api.services.UserService
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController
import core.responses.HttpResponse

@Controller
class UserController(private val userService: UserService) : BaseController {
    @Mapping(path = "/users", method = RequestMethod.GET, queryParams = true, authentication = true)
    fun getUser(exchange: HttpExchange, mapOfQuerys: Map<String, String>) {
        if (!mapOfQuerys.containsKey("id")) {
            HttpResponse.send(
                exchange = exchange,
                json = Json(
                    message = "Bad Request !! " + "Query parameter id is required !!",
                    code = StatusCode.BadRequest.code
                )
            )
            return
        }
        HttpResponse.send(
            exchange = exchange,
            json = Json(
                message = "User found !!",
                code = StatusCode.Ok.code,
                data = userService.getUserById(mapOfQuerys["id"]!!.toInt())
            )
        )
    }

    @Mapping(path = "/users/", method = RequestMethod.GET, queryParams = false, authentication = false)
    fun getUserList(exchange: HttpExchange) {
        HttpResponse.send(
            exchange = exchange,
            json = Json(
                message = "Users found !!", code = StatusCode.Ok.code,
                data = listOf(
                    User(1, "teste"), User(2, "teste"), User(3, "teste")
                )
            ),
        )
    }
}