package com.example.ashaapp.room.allschemes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AllSchemesEntity::class], version = 1, exportSchema = true)
abstract class AllSchemesDB : RoomDatabase() {
    abstract fun dao(): AllSchemesDAO

    companion object {
        private var INSTANCE: AllSchemesDB? = null
        fun getDatabase(context: Context): AllSchemesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AllSchemesDB::class.java,
                        "all_schemes_database"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}