package core.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import java.util.*

class JwtCreator(private val secretKey: String) {
    fun createJwt(subject: String): String {
        try {
            val algorithm: Algorithm = Algorithm.HMAC256(secretKey)
            val token: String = JWT.create().withSubject(subject).withExpiresAt(Date(System.currentTimeMillis() + 60000)).sign(algorithm)
            return token
        } catch (e: JWTCreationException) {
            return ""
        }
    }
}