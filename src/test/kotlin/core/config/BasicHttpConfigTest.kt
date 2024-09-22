package core.config

import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import shared.BaseTest


class BasicHttpConfigTest: BaseTest() {
    @Test
    fun `should get all users and return 200`() {
        get("/users/")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .body("message", equalTo("Users found !!"))
            .body(
                "data", equalTo(
                    listOf(
                        mapOf("id" to 1, "name" to "teste"),
                        mapOf("id" to 2, "name" to "teste"),
                        mapOf("id" to 3, "name" to "teste")
                    )
                )
            )
    }

    @Test
    fun `should get a user and return 200`() {
        given().auth().basic("user", "pass").`when`()
            .get("/users?id=300")
            .then()
            .assertThat()
            .contentType("application/json")
            .body("message", equalTo("User found !!"))
            .statusCode(200)
            .body(
                "data", equalTo(
                    mapOf("id" to 300, "name" to "teste")
                )
            )
    }

    @Test
    fun `should post a user and return 201`() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"id\": 200, \"name\": \"teste\" }")
            .`when`()
            .post("/users/create")
            .then()
            .assertThat()
            .contentType("application/json")
            .statusCode(201)
            .body("message", equalTo("User created"))
            .body("code", equalTo(201))
            .body("data", equalTo(mapOf("id" to 200, "name" to "teste")))
    }

    @Test
    fun `should not get a user because id query param is not in request and return 400`() {
        given().auth().basic("user", "pass").`when`()
            .get("/users?identificador=300")
            .then()
            .assertThat()
            .contentType("application/json")
            .body("message", equalTo("Bad Request !! " + "Query parameter id is required !!"))
            .statusCode(400)
    }

    @Test
    fun `should not get a user because id query param is not in correct format and return 400`() {
        given().auth().basic("user", "pass").`when`()
            .get("/users?id=teste")
            .then()
            .assertThat()
            .contentType("application/json")
            .body("message", equalTo("Bad Request !! " + "Query parameter id is not in the correct format !!"))
            .statusCode(400)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun `before all`() {
            BasicHttpConfig.startServer()
            baseURI = "http://localhost:3000"
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            BasicHttpConfig.stopServer()
        }
    }
}