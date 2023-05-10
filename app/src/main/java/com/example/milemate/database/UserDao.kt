package com.example.milemate.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String) : User?


}