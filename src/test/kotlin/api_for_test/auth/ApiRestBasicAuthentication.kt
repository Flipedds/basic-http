package api_for_test.auth

import com.sun.net.httpserver.BasicAuthenticator

class ApiRestBasicAuthentication(realm: String?) : BasicAuthenticator(realm) {
    override fun checkCredentials(user: String, pwd: String): Boolean {
        return user == "user" && pwd == "pass"
    }
}