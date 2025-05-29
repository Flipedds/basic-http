package com.example.services

import com.example.entities.User
import com.example.interfaces.IUserRepository
import com.example.interfaces.IUserService


class UserService(private val userRepository: IUserRepository): IUserService {
    override fun getUserById(id: Int) : User {
        return userRepository.getUserById(id)
    }
}