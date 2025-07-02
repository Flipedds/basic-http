package core.domain.authentication

/**
 * Interface for creating JWT token
 */
interface IJwtCreator {
    fun createJwt(subject: String): String
}