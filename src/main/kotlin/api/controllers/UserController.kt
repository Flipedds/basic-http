package api.controllers

import com.sun.net.httpserver.HttpExchange
import core.annotations.Mapping
import core.annotations.Controller
import core.catchers.ResponseCatchers.Companion.ok
import core.catchers.ResponseCatchers.Companion.badRequest
import core.enums.RequestMethod
import core.helpers.Helpers.Companion.jsonify
import api.entities.User
import api.services.UserService

@Controller
class UserController(private val userService: UserService) {
    @Mapping(path = "/users", method = RequestMethod.GET, queryParams = true, authentication = true)
    fun getUser(exchange: HttpExchange, mapOfQuerys: Map<String, String>) {
        if(!mapOfQuerys.containsKey("id")){
            badRequest(exchange, "Query parameter id is required")
            return
        }
        val json = jsonify(userService.getUserById(mapOfQuerys["id"]!!.toInt()))
        ok(exchange, json)
    }

    @Mapping(path = "/list", method = RequestMethod.GET, queryParams = false, authentication = false)
    fun getUserList(exchange: HttpExchange) {
        val json = jsonify(
            listOf(
                User(1, "teste"),
                User(2, "teste"),
                User(3, "teste")
            )
        )
        ok(exchange, json)
    }
}