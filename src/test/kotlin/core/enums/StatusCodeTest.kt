package core.enums

import org.junit.jupiter.api.Test
import shared.BaseTest
import core.domain.response.StatusCode

class StatusCodeTest: BaseTest() {
    @Test
    fun `should get a correct code for Ok`() {
        200 eq StatusCode.Ok.code
    }

    @Test
    fun `should get a correct code for Created`() {
        201 eq StatusCode.Created.code
    }

    @Test
    fun `should get a correct code for BadRequest`() {
        400 eq StatusCode.BadRequest.code
    }

    @Test
    fun `should get a correct code for NotFound`() {
        404 eq StatusCode.NotFound.code
    }

    @Test
    fun `should get a correct code for NotAllowed`() {
        405 eq StatusCode.NotAllowed.code
    }

    @Test
    fun `should get a correct code for InternalServerError`() {
        500 eq StatusCode.InternalServerError.code
    }
}