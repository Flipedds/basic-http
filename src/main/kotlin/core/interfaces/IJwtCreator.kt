package core.interfaces

/**
 * Interface for creating JWT token
 */
interface IJwtCreator {
    fun createJwt(subject: String): String
}