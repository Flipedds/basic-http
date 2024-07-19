package api.services

import api.entities.User
import api.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {
    fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}