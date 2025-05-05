package entities_for_test

import core.annotations.Injectable
import database.interfaces.IBasicOrm
import database.annotations.Column
import database.annotations.Id
import database.annotations.Table
import database.config.BasicOrm
import database.enums.GeneratedBy

@Table(name = "tb_test")
class TbTest {
    @Id(GeneratedBy.APPLICATION)
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "name")
    var name: String = ""

    override fun toString(): String {
        return "TbTest(id=$id, name='$name')"
    }
}
@Injectable
interface ITbTestOrm: IBasicOrm<TbTest>

class TbTestOrm: BasicOrm<TbTest>(TbTest::class), ITbTestOrm

