package com.carnet.restaurants.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val adresse: String = "",
    val telephone: String = "",
    val cuisine: String = "",
    val notes: String = "",
    val photoUri: String = "",
    val notation: Float = 0f,
    val dateAjout: Long = System.currentTimeMillis()
)
