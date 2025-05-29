package com.example.entities

import com.example.dtos.TbTestDto
import database.annotations.Column
import database.annotations.Id
import database.annotations.JoinColumn
import database.annotations.Table
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

    fun toDto(): TbTestDto {
        return TbTestDto(id, name)
    }
}