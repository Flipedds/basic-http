### BASIC-HTTP
Basic implementation of a Back-End Web framework in Kotlin and Gradle

### Using with Gradle
````groove
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.Flipedds:basic-http:cf6da64'
}

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
import core.annotations.*
import core.enums.RequestMethod
import core.domain.Json
import core.enums.StatusCode
import core.interfaces.BaseController


@Controller
class UserController(private val userService: IUserService) : BaseController {
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
