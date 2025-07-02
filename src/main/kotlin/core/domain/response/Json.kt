package core.domain.response

/**
 * This class is a generic class that wraps the response of the API.
 * It contains the message, the status code and the data.
 */
data class Json<T>(
    val message: String,
    val code: Int,
    val data: T? = null
){
    constructor(message: String, statusCode: StatusCode, data: T? = null)
            : this(message, statusCode.code, data)
}