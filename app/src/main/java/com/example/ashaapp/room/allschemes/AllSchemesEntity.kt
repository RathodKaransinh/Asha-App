package com.example.ashaapp.room.allschemes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllSchemesEntity")
data class AllSchemesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @field:ColumnInfo(name = "schemes_code_ab")
    var scheme_code_ab: String,

    @field:ColumnInfo(name = "schemes_absolute_name")
    var schemes_name_ab: String,

    @field:ColumnInfo(name = "schemes_ab_value")
    var value_ab: Long,
)
