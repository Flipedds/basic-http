package core.extensions

import com.sun.net.httpserver.HttpExchange
import core.domain.Json
import core.helpers.Helpers.Companion.jsonify

fun HttpExchange.send(json: Json<Any>) {
    val jsonString = jsonify(json)
    val statusCode = json.code
    this.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
    this.responseHeaders.set("Access-Control-Allow-Origin", "*")
    this.sendResponseHeaders(statusCode, jsonString.length.toLong())
    val os = this.responseBody
    os.write(jsonString.toByteArray())
    os.close()
}