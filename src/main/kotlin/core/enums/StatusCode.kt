package core.enums

enum class StatusCode(val code: Int) {
    Ok(200),
    NotFound(404),
    NotAllowed(405),
    BadRequest(400),
    InternalServerError(500),
}