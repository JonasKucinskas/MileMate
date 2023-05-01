package com.example.milemate.database


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.LifecycleOwner

class DBManager(application: Application) : AndroidViewModel(application) {
    private var database : CarDatabase


    init{
        database = CarDatabase.getDatabase(application)
    }

    fun insertCar(car : com.example.milemate.Car){
        viewModelScope.launch(Dispatchers.IO) {


            val carToInsert = Car(0, car.carName, car.carBrand, car.carMileage.toInt(), null, null)
            database.carDao().insertCar(carToInsert)
        }
    }


    fun deleteCar(car : Car){
        viewModelScope.launch(Dispatchers.IO) {
            database.carDao().deleteCar(car)
        }
    }

    fun getCarsCount() : Int{
        var count : Int = 0
        viewModelScope.launch(Dispatchers.IO) {
            count = database.carDao().getCarsCount()
        }
        return count
    }

    fun getCar(id : Int) : LiveData<Car>{

        val result = MutableLiveData<Car>()
        viewModelScope.launch(Dispatchers.IO) {
            var car : Car = database.carDao().getCar(id)
            result.postValue(car)
        }
        return result
    }

    fun getAllCars(): LiveData<List<Car>> {
        return database.carDao().getAllCars()
    }

    fun updateCar(car: Car){
        viewModelScope.launch(Dispatchers.IO){
            database.carDao().updateCar(car)
        }
    }

    fun updateMileage(carId: Int, newMileage: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var carToUpdate = database.carDao().getCar(carId)
            carToUpdate.mileage = newMileage
            database.carDao().updateCar(carToUpdate)
        }
    }

    fun updateBrand(carId: Int, newBrand: String){
        viewModelScope.launch(Dispatchers.IO) {
            var carToUpdate = database.carDao().getCar(carId)
            carToUpdate.brand = newBrand
            database.carDao().updateCar(carToUpdate)
        }
    }

    fun updateName(carId: Int, newName: String){
        viewModelScope.launch(Dispatchers.IO) {
            var carToUpdate = database.carDao().getCar(carId)
            carToUpdate.name = newName
            database.carDao().updateCar(carToUpdate)
        }
    }


    /*val allItems: LiveData<List<Car : car>>
    fun getAllItems(): LiveData<List<>> {
        return CarDatabase.getAllItems()
    }*/

    //Checks if data (cars) is present in database
    fun hasData(): Boolean {
        val db = this.database
        val countQuery = "SELECT count(*) FROM $CarDatabase"
        val cursor = db.query(countQuery, null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }
}