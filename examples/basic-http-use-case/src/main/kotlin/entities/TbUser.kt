package com.example.entities

import database.annotations.Column
import database.annotations.Id
import database.annotations.Table
import database.enums.GeneratedBy

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