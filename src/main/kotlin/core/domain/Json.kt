package core.domain

import core.enums.StatusCode

data class Json<T>(
    val message: String,
    val code: Int,
    val data: T? = null
){
    constructor(message: String, statusCode: StatusCode, data: T? = null)
            : this(message, statusCode.code, data)
}