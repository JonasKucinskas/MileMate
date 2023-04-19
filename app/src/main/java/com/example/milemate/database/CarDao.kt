package com.example.milemate.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CarDao {
    @Insert
    fun insertCar(car: Car)

    @Insert
    fun updateLicenceDate(car: Car)

    @Query("SELECT count(*) FROM car")
    fun getCarsCount() : Int

    @Query("SELECT * FROM car WHERE id = :id LIMIT 1")
    fun getCar(id : Int) : Car

    @Delete
    fun deleteCar(car: Car)
    @Query("SELECT * FROM car")
    fun getAllCars(): LiveData<List<Car>>
}