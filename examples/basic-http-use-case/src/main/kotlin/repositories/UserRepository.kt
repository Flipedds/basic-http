package com.example.repositories

import com.example.entities.User
import com.example.interfaces.IUserRepository

class UserRepository: IUserRepository {
    override fun getUserById(id: Int) : User {
        return User(id = id, name = "teste")
    }
}