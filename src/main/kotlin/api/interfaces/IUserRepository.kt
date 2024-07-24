package api.interfaces

import api.entities.User

interface IUserRepository {
    fun getUserById(id: Int) : User
}