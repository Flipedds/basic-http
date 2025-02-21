package core.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import core.interfaces.IJwtCreator
import java.util.*

/**
 * Class to create a JWT token.
 * @param secretKey The secret key to sign the token.
 */
class JwtCreator(private val secretKey: String): IJwtCreator {
    override fun createJwt(subject: String): String {
        try {
            val algorithm: Algorithm = Algorithm.HMAC256(secretKey)
            val token: String = JWT.create().withSubject(subject).withExpiresAt(Date(System.currentTimeMillis() + 60000)).sign(algorithm)
            return token
        } catch (e: JWTCreationException) {
            return ""
        }
    }
}