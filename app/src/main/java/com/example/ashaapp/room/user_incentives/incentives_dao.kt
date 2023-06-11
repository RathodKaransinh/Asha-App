package com.example.ashaapp.room.user_incentives

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncentivesDao {
    @Insert
    fun insert(scheme: IncentivesEntity)

    @Query("SELECT * FROM UserIncentives")
    fun schemes(): LiveData<List<IncentivesEntity>?>

    @Query("SELECT * FROM UserIncentives WHERE isApproved=0")
    fun notApprovedSchemes(): LiveData<List<IncentivesEntity>?>

    @Query("SELECT * FROM UserIncentives WHERE state=0")
    fun offlineSchemes(): List<IncentivesEntity>?

    @Query("UPDATE UserIncentives set state=1 where state=0")
    fun updateState()

    @Query("DELETE FROM UserIncentives")
    fun truncate()

    @Query("DELETE FROM UserIncentives where state=1")
    fun deleteOnlineSchemes()

    @Query("DELETE FROM UserIncentives where id=:id")
    fun deleteScheme(id: Int)

    @Query("SELECT * FROM UserIncentives WHERE isApproved=1")
    fun approvedSchemes(): List<IncentivesEntity>?
}