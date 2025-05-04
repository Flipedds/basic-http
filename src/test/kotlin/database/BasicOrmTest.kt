package database

import entities_for_test.TbTestOrm
import entities_for_test.TbTest
import org.junit.jupiter.api.Test
import shared.BaseTest
import kotlin.test.Ignore

class BasicOrmTest: BaseTest() {
    @Test
    fun `should create a register on db`() {
        val tbTest = TbTest()
        tbTest.id = 3
        tbTest.name = "test"

        TbTestOrm().insert(tbTest)
    }

    @Test
    @Ignore
    fun `should return all registers on db`() {
        val findAll = TbTestOrm().findAll<TbTest>()
        println(findAll)
    }
}