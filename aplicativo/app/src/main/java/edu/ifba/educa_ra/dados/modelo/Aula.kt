package edu.ifba.educa_ra.dados.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aulas")
data class AulaModelo(
    @PrimaryKey
    val id: String,
    val nome: String,
    val detalhes: String,
    val disciplina_id: String
)