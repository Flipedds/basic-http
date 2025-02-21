package core.interfaces

interface IJwtCreator {
    fun createJwt(subject: String): String
}