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
    val siteWeb: String = "",
    val cuisine: String = "",
    val notes: String = "",
    val photoUri: String = "",
    val notation: Float = 0f,
    // 0 = non défini, 1 = Pas cher, 2 = Bon prix, 3 = Très cher
    val prixFourchette: Int = 0,
    val dateAjout: Long = System.currentTimeMillis()
)
