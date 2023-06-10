package com.example.ashaapp.room.notapprovedschemes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NotApprovedSchemesEntity")
data class NotApprovedSchemesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "req_scheme_name")
    var req_scheme_name: String,

    @ColumnInfo(name = "req_time")
    var req_date: Long,

    @ColumnInfo(name = "value_of_schemes")
    var value_of_schemes: Long,

    @ColumnInfo(name = "state")
    var internetstate: Boolean,
)
