package edu.ifba.educa_ra.dados.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ifba.educa_ra.dados.modelo.DisciplinaModelo

@Dao
interface DisciplinaDao {

    @Query("SELECT * FROM disciplinas")
    fun buscaTodos() : List<DisciplinaModelo>

    @Query("SELECT * FROM disciplinas WHERE id = :id")
    fun buscaPorId(id: Long) : DisciplinaModelo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg disciplina: DisciplinaModelo)

    @Delete
    fun remove(disciplina: DisciplinaModelo)
}