package com.carnet.restaurants.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carnet.restaurants.model.Commentaire

@Dao
interface CommentaireDao {

    @Query("SELECT * FROM commentaires WHERE restaurantId = :restaurantId ORDER BY date DESC")
    fun getForRestaurant(restaurantId: Long): LiveData<List<Commentaire>>

    @Insert
    suspend fun insert(commentaire: Commentaire)

    @Delete
    suspend fun delete(commentaire: Commentaire)
}
