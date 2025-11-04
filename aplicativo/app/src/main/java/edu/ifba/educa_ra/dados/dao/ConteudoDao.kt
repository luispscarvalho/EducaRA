package edu.ifba.educa_ra.dados.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ifba.educa_ra.dados.modelo.AulaModelo
import edu.ifba.educa_ra.dados.modelo.ConteudoModelo

@Dao
interface ConteudoDao {

    @Query("SELECT * FROM conteudos")
    fun buscaTodos() : List<ConteudoModelo>

    @Query("SELECT * FROM conteudos WHERE id = :id")
    fun buscaPorId(id: String) : ConteudoModelo?

    @Query("SELECT * FROM conteudos WHERE aula_id = :id")
    fun buscaPorAula(id: String) : List<ConteudoModelo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg conteudo: ConteudoModelo)

    @Delete
    fun remove(conteudo: ConteudoModelo)
}