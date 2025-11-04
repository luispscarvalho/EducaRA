package edu.ifba.educa_ra.dados.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ifba.educa_ra.dados.modelo.AulaModelo

@Dao
interface AulaDao {

    @Query("SELECT * FROM aulas")
    fun buscaTodos() : List<AulaModelo>

    @Query("SELECT * FROM aulas WHERE id = :id")
    fun buscaPorId(id: String) : AulaModelo?

    @Query("SELECT * FROM aulas WHERE disciplina_id = :id")
    fun buscaPorDisciplina(id: String) : List<AulaModelo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg aula: AulaModelo)

    @Delete
    fun remove(aula: AulaModelo)
}