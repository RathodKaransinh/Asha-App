package com.example.ashaapp.room.approvedschemes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ApprovedSchemesEntity::class], version = 1, exportSchema = false)
abstract class ApprovedSchemesDB : RoomDatabase() {
    abstract fun dao(): ApprovedSchemesDAO

    companion object {
        private var INSTANCE: ApprovedSchemesDB? = null
        fun getDatabase(context: Context): ApprovedSchemesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        ApprovedSchemesDB::class.java,
                        "approved_database"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}