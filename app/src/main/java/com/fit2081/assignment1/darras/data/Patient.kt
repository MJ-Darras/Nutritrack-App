package com.fit2081.assignment1.darras.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a patient entity in the database.
 */
@Entity (tableName = "patients")
data class Patient(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userID: String,

    val phoneNumber: String,

    val password: String,

    val registered: Boolean,

    val name: String,

    val sex: String,

    val heifaTotalscore: Float,

    val discretionaryHEIFAscore: Float,

    val vegetablesHEIFAscore: Float,

    val fruitHEIFAscore: Float,

    val grainsandcerealsHEIFAscore: Float,

    val wholegrainsHEIFAscore: Float,

    val meatandalternativesHEIFAscore: Float,

    val dairyandalternativesHEIFAscore: Float,

    val sodiumHEIFAscore: Float,

    val alcoholHEIFAscore: Float,

    val waterHEIFAscore: Float,

    val sugarHEIFAscore: Float,

    val saturatedFatHEIFAscore: Float,

    val unsaturatedFatHEIFAscore: Float,

    val aiResponses: String = ""
)
