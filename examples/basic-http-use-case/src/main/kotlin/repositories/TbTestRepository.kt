package com.example.repositories

import com.example.entities.TbTest
import com.example.interfaces.ITbTestRepository
import database.config.BasicOrm

// injectable orm implementation
class TbTestRepository: BasicOrm<TbTest>(TbTest::class), ITbTestRepository