package com.example.coffepath.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile

@Database(entities = [Cafe::class, Review::class,UserProfile::class],version = 2,exportSchema = false)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun cafeDao(): CafeDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `reviews` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `cafe_id` INTEGER NOT NULL,
                        `user_id` TEXT NOT NULL,
                        `rating` REAL NOT NULL,
                        `comment` TEXT NOT NULL,
                        `photo_uri` TEXT,
                        `drink_type` TEXT,
                        `timestamp` INTEGER NOT NULL,
                        FOREIGN KEY(`cafe_id`) REFERENCES `cafes`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_reviews_cafe_id` ON `reviews` (`cafe_id`)
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `user_id` TEXT PRIMARY KEY NOT NULL,
                        `name` TEXT NOT NULL,
                        `email` TEXT,
                        `photo_uri` TEXT,
                        `reviews_count` INTEGER NOT NULL DEFAULT 0,
                        `favorites_count` INTEGER NOT NULL DEFAULT 0,
                        `visited_cafes_count` INTEGER NOT NULL DEFAULT 0,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        fun getDatabase(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }
}






