package com.example.services

import com.example.dtos.TbTestDto
import com.example.interfaces.ITbTestRepository
import com.example.interfaces.ITbTestService

class TbTestService(private val tbTestOrm: ITbTestRepository) : ITbTestService {
    override fun getTbTestById(id: Int): TbTestDto {
        val findOne = tbTestOrm.findOne(id)

        if (findOne == null) {
            throw IllegalArgumentException("TbTest with id $id not found")
        }

        return findOne.toDto()
    }
}