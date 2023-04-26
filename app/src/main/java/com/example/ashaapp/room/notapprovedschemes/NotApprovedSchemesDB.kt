package com.example.ashaapp.room.notapprovedschemes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotApprovedSchemesEntity::class], version = 1, exportSchema = false)
abstract class NotApprovedSchemesDB : RoomDatabase() {
    abstract fun dao(): NotApprovedSchemesDAO

    companion object {
        private var INSTANCE: NotApprovedSchemesDB? = null
        fun getDatabase(context: Context): NotApprovedSchemesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        NotApprovedSchemesDB::class.java,
                        "not_approved_database"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}