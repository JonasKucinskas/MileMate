package com.example.milemate.database

import android.util.Log
import com.google.firebase.database.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FireBase() {

    private var dbRef : DatabaseReference = FirebaseDatabase.getInstance("https://milemate-453bc-default-rtdb.europe-west1.firebasedatabase.app").reference

    fun addUser(email : String, password : String){
        // Remove @ and . from email, because these symbols cannot be stored on the database.
        val escapedEmail = escapeEmail(email)
        dbRef.child(escapedEmail).child("password").setValue(sha256(password))
    }

    fun authenticateUser(email : String, password : String, callback: (authSuccess: Boolean) -> Unit){
        val escapedEmail = escapeEmail(email)
        var authSuccess = false
        dbRef.child(escapedEmail).child("password").get().addOnSuccessListener {
            // Hash given password and compare it to the password in the database.
            val hashedPassword = sha256(password)
            authSuccess = hashedPassword == it.value
            callback(authSuccess)
        }
    }

    fun userExist(email: String, callback: (exist: Boolean) -> Unit){
        val escapedEmail = escapeEmail(email)
        var exist = false

        dbRef.child(escapedEmail).child("password").get().addOnSuccessListener {
            // If password is set, then the user already exist
            if(it.value != null){
                exist = true
            }
            callback(exist)
        }
    }

    fun insertCar(email: String, car: com.example.milemate.Car){
        val escapedEmail = escapeEmail(email)

        getNewCarID(email){
            val path = dbRef.child(escapedEmail).child("cars").child((it).toString())
            path.child("name").setValue(car.carName)
            path.child("model").setValue(car.carBrand)
            path.child("mileage").setValue(car.carMileage)
        }
    }

    fun getCar(email: String, carID: Int, callback: (car: Car) -> Unit){
        val escapedEmail = escapeEmail(email)
        val path = dbRef.child(escapedEmail).child("cars").child(carID.toString())

        path.get().addOnSuccessListener {
            val name: String = it.child("name").value.toString()
            val model: String = it.child("model").value.toString()
            val mileage: Int = Integer.parseInt(it.child("mileage").value.toString())
            val checkupReminder: String = it.child("checkupReminder").value.toString()
            val checkupDate: String = it.child("checkupDate").value.toString()

            callback(Car(carID, name, model, mileage, checkupReminder, checkupDate))
        }
    }

    fun getUserCars(email: String, callback: (exist: ArrayList<Car>) -> Unit){
        val escapedEmail = escapeEmail(email)
        val path = dbRef.child(escapedEmail).child("cars")
        var cars : ArrayList<Car> = ArrayList()

        path.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(car in snapshot.children){
                    Log.e("FIREBASE DATA FETCH", "Value: ${car.value}")

                    var mileage : Int = 0

                    if(car.child("mileage").value != null){
                        mileage = Integer.parseInt(car.child("mileage").value.toString())
                    }

                    cars.add(Car(Integer.parseInt(car.key),
                        car.child("name").value.toString(),
                        car.child("model").value.toString(),
                        mileage,
                        car.child("checkupReminder").value.toString(),
                        car.child("checkupDate").value.toString()
                    ))
                }
                callback(cars)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun getNewCarID(email: String, callback: (newid: Int) -> Unit){
        val escapedEmail = escapeEmail(email)
        val path = dbRef.child(escapedEmail).child("cars")

        path.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var biggestID = 1
                for(id in snapshot.children) {

                    val idValue : Int? = id.key?.let { Integer.parseInt(it) }
                    if(biggestID < idValue!!){
                        biggestID = idValue
                    }
                }
                callback(biggestID+1)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun escapeEmail(email: String) : String{
        return email.replace("@", "").replace(".", "")
    }

    private fun sha256(string: String): String {
        var digest: MessageDigest? = null
        var hash = ""
        try {
            digest = MessageDigest.getInstance("SHA-256")
            digest.update(string.toByteArray())
            val bytes: ByteArray = digest.digest()
            val sb = StringBuffer()
            for (i in bytes.indices) {
                val hex = Integer.toHexString(0xFF and bytes[i].toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }
            hash = sb.toString()
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }
        return hash
    }
}