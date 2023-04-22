package com.example.ashaapp.room.allschemes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AllSchemesDAO {
    @Insert
    fun insert(scheme: AllSchemesEntity)

    @Query("SELECT schemes_ab_value FROM AllSchemesEntity where schemes_absolute_name=:name")
    fun getprize(name: String): Long

    @Query("SELECT schemes_absolute_name FROM AllSchemesEntity")
    fun getallschemes(): List<String>

    @Query("SELECT schemes_code_ab FROM AllSchemesEntity")
    fun getallschemescode(): List<String>

    @Query("DELETE FROM AllSchemesEntity")
    fun truncate()

    @Query("SELECT schemes_absolute_name FROM AllSchemesEntity where schemes_code_ab=:code")
    fun filterSchemesWithCode(code: String): List<String>
}