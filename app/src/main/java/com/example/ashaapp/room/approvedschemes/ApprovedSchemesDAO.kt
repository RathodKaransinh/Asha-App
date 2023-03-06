package com.example.ashaapp.room.approvedschemes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ApprovedSchemesDAO {
    @Insert
    fun insert(apScheme: ApprovedSchemesEntity)

    @Query("SELECT * FROM ApprovedSchemesEntity")
    fun getalldata(): List<ApprovedSchemesEntity>?

    @Query("DELETE FROM ApprovedSchemesEntity")
    fun truncate()
}