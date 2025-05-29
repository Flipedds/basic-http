package com.example.interfaces

import com.example.entities.User
import core.annotations.Injectable

@Injectable
interface IUserRepository {
    fun getUserById(id: Int) : User
}