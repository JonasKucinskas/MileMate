package com.example.milemate.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface CarDao {
    //@Query("SELECT * FROM car")
    //fun getAllCars();

    @Insert
    fun insertCar(car: Car)
}