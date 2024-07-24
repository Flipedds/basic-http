package api.services

import api.entities.User
import api.interfaces.IUserRepository
import api.interfaces.IUserService

class UserService(private val userRepository: IUserRepository): IUserService {
    override fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}