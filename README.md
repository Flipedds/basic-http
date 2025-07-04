<h1 align="center" style="font-weight: bold;">BASIC-HTTP 🌐</h1>

<p align="center">
 <a href="#technologies">Technologies</a> • 
<a href="#docs">Documentations</a> • 
   <a href="#diagram">Project diagram</a> • 
 <a href="#started">Getting Started</a> •
 <a href="#colab">Collaborators</a> •
 <a href="#contribute">Contribute</a>
</p>

<p align="center">
    <b>Basic implementation of a Back-End Web framework in Kotlin and Gradle
</b>
</p>

<h2 id="technologies">💻 Technologies</h2>

- Kotlin
- Gradle

<h2 id="docs">💻 Documentations</h2>

<a href="https://basic-http.vercel.app/">📚 Basic-http Web Framework documentation</a> <br>
<a href="https://kotlinlang.org/">📚 Kotlin documentation</a> <br>
<a href="https://gradle.org/">📚 Gradle documentation</a>

<h2 id="diagram">Project diagram</h2>

<img src= "https://github.com/user-attachments/assets/53d62b7f-554c-433b-bc10-662544dff858">

<h2 id="started">🚀 Getting started</h2>

<h3>Prerequisites</h3>

- [Intellij](https://github.com/)
- [Gradle 8 +](https://github.com)
- [JDK 21 +](https://github.com)

<h3>Cloning</h3>

```bash
git clone project-url
cd project-name
```

<h3>Starting</h3>

### Using with Gradle
````groovy
repositories {
    mavenCentral()
    maven {
            url = uri("https://maven.pkg.github.com/flipedds/basic-http")
            credentials {
                username = "flipedds"
                password = "a_token_for_read_packages_on_github_contact_me_for_it"
            }
        }
}

dependencies {
    implementation 'com.flipedds:basic-http:2.0'
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
````kotlin
fun main() {
    BasicHttpConfig.startServer()
}
````

### Entity Definition
````kotlin
data class User(val id: Int, val name: String)
````

### Controller Definition

````kotlin
@Controller
class UserController(private val userService: IUserService) : BaseController,
    IJwtCreator by JwtCreator("SECRET_KEY"){
    @Mapping(path = "/users/auth", method = RequestMethod.POST)
    fun authUser(): Json<String>{
        return Json(
                message = "User authenticated !!",
                statusCode = StatusCode.Ok,
                data = createJwt("user")
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
````kotlin
@Injectable
interface IUserService {
    fun getUserById(id: Int) : User
}

class UserService(private val userRepository: IUserRepository): IUserService {
    override fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}

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

### Orm Config

#### Add server.properties on main properties folder => src/main/resources/server.properties

````properties
database.url=jdbc:mysql://localhost:3306/database_name
database.username=root
database.password=root
````


### Orm Methods
````kotlin
interface IBasicOrm<T : Any> {
    fun insert(entity: T)
    fun findAll(): MutableList<T>
    fun findOne(id: Any): T?
    fun updateOne(entity: T)
    fun deleteOne(entity: T)
    fun closeConnection()
}
````

### Orm Use Definition
````kotlin
// entity class of orm, required empty constructor
@Table(name = "tb_test")
class TbTest {
    constructor(user: TbUser?, name: String, id: Int) {
        this.user = user
        this.name = name
        this.id = id
    }
    
    constructor()
        
    @Id(GeneratedBy.APPLICATION)
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "name")
    var name: String = ""

    @JoinColumn(name = "user_id", type = Relation.ManyToOne)
    var user: TbUser? = null

    override fun toString(): String {
        return "TbTest(id=$id, name='$name', user=$user)"
    }
}

// injectable config
@Injectable
interface ITbTestOrm: IBasicOrm<TbTest>

// injectable implementation
class TbTestOrm: BasicOrm<TbTest>(TbTest::class), ITbTestOrm

// orm injectable use
@Injectable
interface ITbTestService {
    fun getTbTestById(id: Int) : TbTest?
}

class TbTestService(private val tbTestOrm: ITbTestOrm): ITbTestService {
    override fun getTbTestById(id: Int) : TbTest? {
        return tbTestOrm.findOne(id)
    }
}

// orm normal use, if you don't want to use injectable
val tbTest = TbTest()
tbTest.id = 10
tbTest.name = "test"
tbTest.user = TbUser(1,"test", "", 20)

val tbTestOrm = TbTestOrm()
tbTestOrm.insert(tbTest)
````

<h2 id="colab">🤝 Collaborators</h2>

<table>
  <tr>
    <td align="center">
      <a href="#">
        <img src="https://avatars.githubusercontent.com/u/110608654?v=4" width="100px;" alt="Filipe André Profile Picture"/><br>
        <sub>
          <b>Filipe André</b>
        </sub>
      </a>
    </td>
  </tr>
</table>

<h2 id="contribute">📫 Contribute</h2>

1. `git clone project-url`
2. `git checkout -b feature-name`
3. Follow commit patterns
4. Open a Pull Request explaining the problem solved or feature made, if exists, append screenshot of visual modifications and wait for the review!

<h3>Documentations that might help</h3>

[📝 How to create a Pull Request](https://www.atlassian.com/br/git/tutorials/making-a-pull-request)

[💾 Commit pattern](https://gist.github.com/joshbuchea/6f47e86d2510bce28f8e7f42ae84c716)
