package core.domain

import api_for_test.entities.User
import core.domain.response.StatusCode
import org.junit.jupiter.api.Test
import shared.BaseTest
import core.domain.response.Json

class JsonTest: BaseTest() {
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