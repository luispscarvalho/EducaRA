package edu.ifba.educa_ra.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.ifba.educa_ra.dados.dao.AulaDao
import edu.ifba.educa_ra.dados.dao.ConteudoDao
import edu.ifba.educa_ra.dados.dao.DisciplinaDao
import edu.ifba.educa_ra.dados.modelo.AulaModelo
import edu.ifba.educa_ra.dados.modelo.ConteudoModelo
import edu.ifba.educa_ra.dados.modelo.DisciplinaModelo

@Database(entities = [AulaModelo::class, ConteudoModelo::class, DisciplinaModelo::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aulaDao(): AulaDao
    abstract fun conteudoDao(): ConteudoDao
    abstract fun disciplinaDao(): DisciplinaDao

    companion object {
        fun instancia(context: Context) : AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "educara.db"
            ).allowMainThreadQueries()
                .build()
        }
    }
}