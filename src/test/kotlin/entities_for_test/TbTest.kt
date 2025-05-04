package entities_for_test

import database.annotations.Column
import database.annotations.Table
import database.config.BasicOrm

@Table(name = "tb_test")
class TbTest {
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "name")
    var name: String = ""

    override fun toString(): String {
        return "TbTest(id=$id, name='$name')"
    }
}

class TbTestOrm: BasicOrm<TbTest>()