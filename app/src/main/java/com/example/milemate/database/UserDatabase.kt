package com.example.milemate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object {
        private const val DATABASE_NAME = "users.db"

        private var instance: UserDatabase? = null

        fun getInstance(context: Context) : UserDatabase{
            return instance ?: synchronized(this){
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    DATABASE_NAME
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}