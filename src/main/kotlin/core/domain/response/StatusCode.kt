package core.domain.response

/**
 * Enum class for status codes used in the application.
 */
enum class StatusCode(val code: Int) {
    Ok(200),
    Created(201),
    Unauthorized(401),
    NotFound(404),
    NotAllowed(405),
    BadRequest(400),
    InternalServerError(500),
}