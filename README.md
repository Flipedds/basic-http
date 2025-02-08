### BASIC-HTTP
Basic implementation of a Back-End Web framework in Kotlin and Gradle

### Annotations

````java
@Body -> @Body user: User
@Controller -> @Controller class UserController
@Injectable -> @Injectable interface IUserRepository
@Mapping -> @Mapping(path = "/users/auth", method = RequestMethod.POST)
@PathParam -> @PathParam id: Int?
@QueryParam -> @QueryParam("id") id: Int?
@UseAuthentication -> @UseAuthentication fun getUserById
````

### Supported Status Codes

````java
enum class StatusCode(val code: Int)
    
Ok(200)
Created(201)
Unauthorized(401)
NotFound(404)
NotAllowed(405)
BadRequest(400)
InternalServerError(500)
````

### Supported Request Methods

````java
enum class RequestMethod
    
GET
POST
PUT
DELETE
````

### Using with Gradle
````groove
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.Flipedds:basic-http:df63a8a'
}

````

### Server config

#### Add server.properties on main properties folder => src/main/resources/server.properties

````properties
server.host=localhost
server.port=3000
server.jwtkey=SECRET_KEY
````


### Framework Initializer
````java
import core.config.BasicHttpConfig

fun main() {
    BasicHttpConfig.startServer()
}
````

### Entity Definition
````java
data class User(val id: Int, val name: String)
````

### Controller Definition

````java
import core.enums.RequestMethod
import api_for_test.entities.User
import api_for_test.interfaces.IUserService
import core.annotations.*
import core.authentication.JwtCreator
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController


@Controller
class UserController(private val userService: IUserService) : BaseController {
    @Mapping(path = "/users/auth", method = RequestMethod.POST)
    fun authUser(): Json<String>{
        return Json(
                message = "User authenticated !!",
                statusCode = StatusCode.Ok,
                data = JwtCreator("SECRET_KEY").createJwt("user")
        )
    }
    
    @UseAuthentication
    @Mapping(path = "/users/get/{id}", method = RequestMethod.GET)
    fun getUserById(@PathParam id: Int?): Json<User> {
        return Json(
                message = "User found !!",
                statusCode = StatusCode.Ok,
                data = id?.let { userService.getUserById(it) }
        )
    }
    
    @UseAuthentication
    @Mapping(path = "/users", method = RequestMethod.GET)
    fun getUser(@QueryParam("id") id: Int?): Json<User> {
        return Json(
            message = "User found !!",
            statusCode = StatusCode.Ok,
            data = id?.let { userService.getUserById(it) }
        )
    }

    @Mapping(path = "/users/create", method = RequestMethod.POST)
    fun postUser(@Body user: User): Json<User> {
        println(user.id)
        println(user.name)
        return Json(
            message = "User created",
            statusCode = StatusCode.Created,
            data = user
        )
    }

    @Mapping(path = "/users/", method = RequestMethod.GET)
    fun getUserList(): Json<List<User>> {
        return Json(
            message = "Users found !!",
            statusCode = StatusCode.Ok,
            data = listOf(
                User(1, "teste"), User(2, "teste"), User(3, "teste")
            )
        )
    }
}
````

### Controller Injectables Definition
````java

import api_for_test.interfaces.IUserRepository
import api_for_test.interfaces.IUserService
import core.annotations.Injectable

@Injectable
interface IUserService {
    fun getUserById(id: Int) : User
}

class UserService(private val userRepository: IUserRepository): IUserService {
    override fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}

import api_for_test.entities.User
import api_for_test.interfaces.IUserRepository

@Injectable
interface IUserRepository {
    fun getUserById(id: Int) : User
}

class UserRepository: IUserRepository {
    override fun getUserById(id: Int) : User {
        return User(id = id, name = "teste")
    }
}
````
