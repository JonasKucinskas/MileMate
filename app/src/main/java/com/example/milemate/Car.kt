package com.example.milemate

import java.util.Date

class Car constructor(param1: String, param2: String, param3: String, param4: String?, param5: String?) {
    var carName: String = param1
    var carBrand: String = param2
    var carMileage: String = param3
    var checkupReminder: String? = param4
    var checkupDate: String? = param5
}
