package edu.ifba.educa_ra.dados.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conteudos")
data class ConteudoModelo(
    @PrimaryKey
    val id: String,
    val nome: String,
    val detalhes: String,
    val objeto: String,
    val aula_id: String
)