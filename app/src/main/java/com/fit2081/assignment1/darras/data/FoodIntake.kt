package com.fit2081.assignment1.darras.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// This section was inspired from:
// https://stackoverflow.com/questions/70134307/cascade-delete-in-android-room-database-kotlin
@Entity (tableName = "foodIntakeAnswers",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["user_id"],
            childColumns = ["patient_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patient_user_id")]
)

/**
 * Data class representing a food intake answer.
 */
data class FoodIntake(
    //Renamed foreign key to avoid conflicts and confusion
    @PrimaryKey
    @ColumnInfo(name = "patient_user_id")
    val patientUserID: String,

    val fruit: Boolean = false,
    val vegetables: Boolean = false,
    val grains: Boolean = false,
    val redMeat: Boolean = false,
    val seaFood: Boolean = false,
    val poultry: Boolean = false,
    val fish: Boolean = false,
    val eggs: Boolean = false,
    val nuts: Boolean = false,

    val persona: String = "",

    val timeEat: String = DEFAULT_TIME,
    val timeSleep: String = DEFAULT_TIME,
    val timeWake: String = DEFAULT_TIME,
) {
    // to provide default values for time properties
    companion object {
        const val DEFAULT_TIME = "00:00"
    }
}
