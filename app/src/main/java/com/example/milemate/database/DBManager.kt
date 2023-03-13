package com.example.milemate.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBManager(application: Application) : AndroidViewModel(application) {
    private var database : CarDatabase;

    init{
        database = CarDatabase.getDatabase(application);
    }

    fun insertCar(car : com.example.milemate.Car){
        viewModelScope.launch(Dispatchers.IO) {
            var carToInsert = Car(0, car.carName, car.carBrand, 123);
            database.carDao().insertCar(carToInsert);
        }
    }
}