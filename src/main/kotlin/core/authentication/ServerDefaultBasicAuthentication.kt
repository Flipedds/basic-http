package core.authentication

import com.sun.net.httpserver.BasicAuthenticator

class ServerDefaultBasicAuthentication(realm: String?) : BasicAuthenticator(realm) {
    override fun checkCredentials(user: String, pwd: String): Boolean {
        return user == "user" && pwd == "pass"
    }
}