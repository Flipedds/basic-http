package api.interfaces

import api.entities.User
import core.annotations.Injectable

@Injectable
interface IUserService {
    fun getUserById(id: Int) : User
}