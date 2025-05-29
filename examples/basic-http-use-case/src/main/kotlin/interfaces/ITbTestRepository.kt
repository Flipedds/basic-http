package com.example.interfaces

import com.example.entities.TbTest
import core.annotations.Injectable
import database.interfaces.IBasicOrm

// injectable config
@Injectable
interface ITbTestRepository: IBasicOrm<TbTest>