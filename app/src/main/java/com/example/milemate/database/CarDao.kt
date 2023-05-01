package com.example.milemate.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {
    @Insert
    fun insertCar(car: Car)

    @Insert
    fun updateLicenceDate(car: Car)

    @Update
    fun updateCar(car: Car)

    @Query("SELECT count(*) FROM car")
    fun getCarsCount() : Int

    @Query("SELECT * FROM car WHERE id = :id LIMIT 1")
    fun getCar(id : Int) : Car

    @Delete
    fun deleteCar(car: Car)
    @Query("SELECT * FROM car")
    fun getAllCars(): LiveData<List<Car>>
}