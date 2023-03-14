package com.example.milemate.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Car (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val brand: String,
    val mileage: Int
)