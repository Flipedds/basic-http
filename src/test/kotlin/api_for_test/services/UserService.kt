package api_for_test.services

import api_for_test.entities.User
import api_for_test.interfaces.IUserRepository
import api_for_test.interfaces.IUserService

class UserService(private val userRepository: IUserRepository): IUserService {
    override fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}