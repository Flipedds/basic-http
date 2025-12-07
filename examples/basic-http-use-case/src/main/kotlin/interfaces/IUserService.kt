package com.example.interfaces

import com.example.entities.User
import core.domain.di.Injectable

@Injectable
interface IUserService {
    fun getUserById(id: Int): User
}
