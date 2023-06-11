package com.example.ashaapp.room.notapprovedschemes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotApprovedSchemesDAO {
    @Insert
    fun insert(naScheme: NotApprovedSchemesEntity)

    @Query("SELECT * FROM NotApprovedSchemesEntity")
    fun getalldata(): LiveData<List<NotApprovedSchemesEntity>?>

    @Query("SELECT * FROM NotApprovedSchemesEntity WHERE state=0")
    fun offlineSchemes(): List<NotApprovedSchemesEntity>?

    @Query("UPDATE NotApprovedSchemesEntity set state=1 where state=0")
    fun updatestate()

    @Query("DELETE FROM NotApprovedSchemesEntity")
    fun truncate()

    @Query("DELETE FROM NotApprovedSchemesEntity where state=1")
    fun deleteOnlineSchemes()

    @Query("DELETE FROM NotApprovedSchemesEntity where req_scheme_name=:req_scheme_name")
    fun deleteOfflineSchemes(req_scheme_name :String)
}