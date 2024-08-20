package api.interfaces

import api.entities.User
import core.annotations.Injectable

@Injectable
interface IUserRepository {
    fun getUserById(id: Int) : User
}