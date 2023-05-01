package com.example.milemate.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var brand: String,
    var mileage: Int,
    var checkupReminder: String?,
    var checkupDate: String?
)