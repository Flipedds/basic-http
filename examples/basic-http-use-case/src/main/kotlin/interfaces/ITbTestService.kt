package com.example.interfaces

import com.example.dtos.TbTestDto
import core.domain.di.Injectable

// orm injectable use
@Injectable
interface ITbTestService {
    fun getTbTestById(id: Int) : TbTestDto
}