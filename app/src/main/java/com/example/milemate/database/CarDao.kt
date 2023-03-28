package com.example.milemate.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CarDao {
    //@Query("SELECT * FROM car")
    //fun getAllCars();

    @Insert
    fun insertCar(car: Car)
    @Delete
    fun deleteCar(car: Car)
    @Query("SELECT * FROM car")
    fun getAllCars(): LiveData<List<Car>>
}