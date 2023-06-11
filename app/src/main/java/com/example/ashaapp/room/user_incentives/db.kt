package com.example.ashaapp.room.user_incentives

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [IncentivesEntity::class], version = 1, exportSchema = false)
abstract class DB : RoomDatabase() {
    abstract fun dao(): IncentivesDao

    companion object {
        private var INSTANCE: DB? = null
        fun getDatabase(context: Context): DB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        DB::class.java,
                        "incentivesDatabase"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}