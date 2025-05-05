package database

import core.di.DIContainer
import entities_for_test.ITbTestOrm
import entities_for_test.TbTestOrm
import entities_for_test.TbTest
import org.junit.jupiter.api.Test
import shared.BaseTest
import kotlin.test.Ignore

class BasicOrmTest : BaseTest() {
    @Test
    @Ignore
    fun `should create a register on db`() {
        val tbTest = TbTest()
        tbTest.id = 10
        tbTest.name = "test"

        val tbTestOrm = TbTestOrm()
        tbTestOrm.insert(tbTest)
    }

    @Test
    @Ignore
    fun `should return all registers on db`() {
        val tbTestOrm = TbTestOrm()
        val findAll = tbTestOrm.findAll()
        println(findAll)
    }

    @Test
    @Ignore
    fun `should get a bean for ITbTestOrm and return a list of test`() {
        val tbTestOrm: ITbTestOrm = DIContainer.getBean(
            Class.forName("entities_for_test.ITbTestOrm")
        )
        val list = tbTestOrm.findAll()
        println(list)
        val listIsEmpty = list.isEmpty()
        listIsEmpty shouldBe false
    }
}