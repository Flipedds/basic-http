package com.example.interfaces

import com.example.dtos.TbTestDto
import core.annotations.Injectable

// orm injectable use
@Injectable
interface ITbTestService {
    fun getTbTestById(id: Int) : TbTestDto
}