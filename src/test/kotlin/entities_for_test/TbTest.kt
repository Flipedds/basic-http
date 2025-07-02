package entities_for_test

import core.di.Injectable
import database.interfaces.IBasicOrm
import database.annotations.Column
import database.annotations.Id
import database.annotations.JoinColumn
import database.annotations.Table
import database.config.BasicOrm
import database.enums.GeneratedBy
import database.enums.Relation

@Table(name = "tb_test")
class TbTest {
    constructor(user: TbUser?, name: String, id: Int) {
        this.user = user
        this.name = name
        this.id = id
    }

    constructor()

    @Id(GeneratedBy.APPLICATION)
    @Column(name = "id")
    var id: Int = 0
    @Column(name = "name")
    var name: String = ""

    @JoinColumn(name = "user_id", type = Relation.ManyToOne)
    var user: TbUser? = null

    override fun toString(): String {
        return "TbTest(id=$id, name='$name', user=$user)"
    }
}

@Table(name = "user")
class TbUser {
    constructor(id: Int, userName: String, email: String, age: Int) {
        this.id = id
        this.userName = userName
        this.email = email
        this.age = age
    }

    constructor()

    @Id(GeneratedBy.AUT0_INCREMENT)
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "username")
    var userName: String = ""

    @Column(name = "email")
    var email: String = ""

    @Column(name = "age")
    var age: Int = 0

    override fun toString(): String {
        return "TbUser(id=$id, userName='$userName', email='$email', age=$age)"
    }
}

//@Injectable -> for local test uncomment this line
interface ITbTestOrm: IBasicOrm<TbTest>

class TbTestOrm: BasicOrm<TbTest>(TbTest::class), ITbTestOrm

