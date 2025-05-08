package database

import core.di.DIContainer
import entities_for_test.ITbTestOrm
import entities_for_test.TbTestOrm
import entities_for_test.TbTest
import entities_for_test.TbUser
import org.junit.jupiter.api.Test
import shared.BaseTest
import kotlin.test.Ignore

class BasicOrmTest : BaseTest() {
    @Test
    @Ignore // for local test comment this line
    fun `should create a register on db`() {
        val tbTest = TbTest()
        tbTest.id = 10
        tbTest.name = "test"
        tbTest.user = TbUser(1,"test", "", 20)

        val tbTestOrm = TbTestOrm()
        tbTestOrm.insert(tbTest)
    }

    @Test
    @Ignore // for local test comment this line
    fun `should return all registers on db`() {
        val tbTestOrm = TbTestOrm()
        val findAll = tbTestOrm.findAll()
        println(findAll)
    }

    @Test
    @Ignore // for local test comment this line
    fun `should get a bean for ITbTestOrm and return a list of test`() {
        val tbTestOrm: ITbTestOrm = DIContainer.getBean(
            Class.forName("entities_for_test.ITbTestOrm")
        )
        val list = tbTestOrm.findAll()
        println(list)
        val listIsEmpty = list.isEmpty()
        listIsEmpty shouldBe false
    }

    @Test
    @Ignore // for local test comment this line
    fun `should delete a record on db`() {
        val tbTest = TbTest()
        tbTest.id = 1
        tbTest.name = "test"

        val tbTestOrm = TbTestOrm()
        tbTestOrm.deleteOne(tbTest)
    }

    @Test
    @Ignore // for local test comment this line
    fun `should update a record on db`() {
        val tbTest = TbTest()
        tbTest.id = 3
        tbTest.name = "test update"
        tbTest.user = TbUser(2,"test", "", 20)

        val tbTestOrm = TbTestOrm()
        tbTestOrm.updateOne(tbTest)
    }

    @Test
    @Ignore // for local test comment this line
    fun `should close a db connection`() {
        val tbTestOrm = TbTestOrm()
        tbTestOrm.closeConnection()
    }
}