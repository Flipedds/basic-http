package api.interfaces

import api.entities.User

interface IUserService {
    fun getUserById(id: Int) : User
}