package core.interfaces

import com.sun.net.httpserver.HttpExchange
import core.domain.Json

interface Response {
    fun send(exchange: HttpExchange, json: Json<Any>)
}