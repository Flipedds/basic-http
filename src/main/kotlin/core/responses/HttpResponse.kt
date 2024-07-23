package core.responses

import com.sun.net.httpserver.HttpExchange
import core.domain.Json
import core.helpers.Helpers.Companion.jsonify
import core.interfaces.Response

object HttpResponse: Response {
    override fun send(exchange: HttpExchange, json: Json<Any>) {
        val jsonString = jsonify(json)
        val statusCode = json.code
        exchange.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
        exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
        exchange.sendResponseHeaders(statusCode, jsonString.length.toLong())
        val os = exchange.responseBody
        os.write(jsonString.toByteArray())
        os.close()
    }
}