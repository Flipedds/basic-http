package api.repositories

import api.entities.User
import api.interfaces.IUserRepository

class UserRepository: IUserRepository {
    override fun getUserById(id: Int) : User {
        return User(id = id, name = "teste")
    }
}