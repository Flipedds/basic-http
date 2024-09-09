package core.domain

import api_for_test.entities.User
import core.enums.StatusCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonTest {
    private infix fun Any.eq(expected: Any?) = assertEquals(expected, this)

    private val user = User(1, "teste")

    private val json = Json(
        message = "success !!",
        statusCode = StatusCode.Ok,
        data = user)

    @Test
    fun `Message should be equals success`() {
        "success !!" eq json.message
    }

    @Test
    fun `Status code should be equals 200`() {
        200 eq json.code
    }

    @Test
    fun `data should be equals user`() {
        user eq json.data
    }
}