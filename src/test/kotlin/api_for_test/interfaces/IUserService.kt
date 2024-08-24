package api_for_test.interfaces

import api_for_test.entities.User
import core.annotations.Injectable

@Injectable
interface IUserService {
    fun getUserById(id: Int) : User
}