package com.example.milemate.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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