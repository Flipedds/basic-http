package api_for_test.interfaces

import api_for_test.entities.User
import core.di.Injectable

@Injectable
interface IUserService {
    fun getUserById(id: Int) : User
}