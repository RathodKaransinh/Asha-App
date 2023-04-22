package com.example.ashaapp.room.approvedschemes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ApprovedSchemesEntity")
data class ApprovedSchemesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "service_scheme_name")
    var ssname: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "value_of_service_scheme")
    var valueofservicescheme: Long,
)
