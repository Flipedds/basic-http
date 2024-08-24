package api_for_test.repositories

import api_for_test.entities.User
import api_for_test.interfaces.IUserRepository

class UserRepository: IUserRepository {
    override fun getUserById(id: Int) : User {
        return User(id = id, name = "teste")
    }
}