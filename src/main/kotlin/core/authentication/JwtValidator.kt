package core.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException

class JwtValidator(private val secretKey: String){
    fun verifyToken(token: String): Boolean {
        try {
            val algorithm: Algorithm = Algorithm.HMAC256(secretKey)
            JWT.require(algorithm).build().verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }
}