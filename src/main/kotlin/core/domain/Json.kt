package core.domain

data class Json<T>(
    val message: String,
    val code: Int,
    val data: T? = null
)