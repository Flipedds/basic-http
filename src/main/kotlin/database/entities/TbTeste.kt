package database.entities

import database.annotations.Column
import database.annotations.Table

@Table(name = "tb_teste")
class TbTeste {
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "nome")
    var nome: String = ""
}