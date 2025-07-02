package core.server.extensions

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import core.domain.response.Json

/**
 * Kotlin Extension functions for HttpHandler
 */
interface HttpHandlerExtensions {
    fun HttpExchange.send(json: Json<Any>) {
        val jsonString = json.toJsonString()
        val statusCode = json.code
        this.responseHeaders.set("Content-Type", "application/json;charset=utf-8")
        this.responseHeaders.set("Access-Control-Allow-Origin", "*")
        this.sendResponseHeaders(statusCode, jsonString.length.toLong())
        val os = this.responseBody
        os.write(jsonString.toByteArray())
        os.close()
    }

    fun String.toMapIfQuery(): Map<String, String> {
        val result: MutableMap<String, String> = HashMap()
        for (param in this.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (pair.size > 1) {
                result[pair[0]] = pair[1]
            } else {
                result[pair[0]] = ""
            }
        }
        return result
    }

    infix fun String.parseTo(typeForParse: String): Any? {
        val parameterClass = Class.forName(typeForParse)
        return when {
            parameterClass.isAssignableFrom(Int::class.javaObjectType) -> this.toIntOrNull()
            parameterClass.isAssignableFrom(Double::class.javaObjectType) -> this.toDoubleOrNull()
            parameterClass.isAssignableFrom(Boolean::class.javaObjectType) -> this.toBooleanStrictOrNull()
            parameterClass.isAssignableFrom(String::class.javaObjectType) -> this
            else -> throw NotImplementedError()
        }
    }


    fun Json<Any>.toJsonString(): String {
        return Gson().toJson(this)
    }

    fun String.jsonToObject(typeName: String): Any {
        return Gson().fromJson(this, Class.forName(typeName))
    }
}