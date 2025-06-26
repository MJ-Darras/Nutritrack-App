package com.fit2081.assignment1.darras.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The main database class for the application.
 */
@Database(entities = [Patient:: class, FoodIntake:: class], version = 11, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    /**
     * Provides access to the patient data access object
     * @return PatientDao instance for database operations related to patients.
     */
    abstract fun patientDao(): PatientDao

    abstract fun foodIntakeDao(): FoodIntakeDao

    /**
     * Companion object to manage database instance using the Singleton pattern.
     * This ensures only one instance of the database is created throughout the app,
     * which is an important consideration for resource management.
     */
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance    //Return the instance
            }
        }

    }
}