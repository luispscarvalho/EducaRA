package edu.ifba.educa_ra.dados.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplinas")
data class DisciplinaModelo(
    @PrimaryKey
    val id: String,
    val nome: String,
    val detalhes: String
)