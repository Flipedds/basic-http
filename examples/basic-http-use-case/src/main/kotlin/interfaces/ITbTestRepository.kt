package com.example.interfaces

import com.example.entities.TbTest
import core.domain.di.Injectable
import database.interfaces.IBasicOrm

// injectable config
@Injectable
interface ITbTestRepository: IBasicOrm<TbTest>