package api.repositories

import api.entities.User

class UserRepository {
    fun getUserById(id: Int) : User {
        return User(id = id, name = "teste")
    }
}