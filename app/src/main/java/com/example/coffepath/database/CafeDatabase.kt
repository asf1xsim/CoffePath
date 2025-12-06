package com.example.coffepath.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coffepath.model.Cafe

@Database(entities = [Cafe::class], version = 1, exportSchema = false)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun cafeDao(): CafeDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun getDatabase(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}






