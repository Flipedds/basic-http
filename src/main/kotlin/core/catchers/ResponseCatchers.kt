package core.catchers

import com.sun.net.httpserver.HttpExchange
import core.domain.JsonMessage
import core.enums.StatusCode
import core.helpers.Helpers.Companion.jsonify

class ResponseCatchers {
    companion object {
        fun methodNotAllowed(exchange: HttpExchange) {
            val json = jsonify(JsonMessage(message = "Method not allowed !!", StatusCode.NotAllowed.code))
            exchange.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
            exchange.sendResponseHeaders(StatusCode.NotAllowed.code, json.length.toLong())
            val os = exchange.responseBody
            os.write(json.toByteArray())
            os.close()
        }

        fun resourceNotFound(exchange: HttpExchange) {
            val json = jsonify(JsonMessage(message = "Resource Not Found !!", StatusCode.NotFound.code))
            exchange.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
            exchange.sendResponseHeaders(StatusCode.NotFound.code, json.length.toLong())
            val os = exchange.responseBody
            os.write(json.toByteArray())
            os.close()
        }

        fun ok(exchange: HttpExchange, json: String) {
            exchange.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.sendResponseHeaders(StatusCode.Ok.code, json.length.toLong())
            val os = exchange.responseBody
            os.write(json.toByteArray())
            os.close()
        }

        fun badRequest(exchange: HttpExchange, message: String) {
            val json = jsonify(JsonMessage(message = "Bad Request !! $message !!", StatusCode.NotAllowed.code))
            exchange.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
            exchange.sendResponseHeaders(StatusCode.BadRequest.code, json.length.toLong())
            val os = exchange.responseBody
            os.write(json.toByteArray())
            os.close()
        }
    }
}