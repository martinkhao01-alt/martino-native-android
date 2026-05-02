package com.carnet.restaurants.repository

import android.content.Context
import com.carnet.restaurants.database.AppDatabase
import com.carnet.restaurants.model.Commentaire
import com.carnet.restaurants.model.Restaurant

class RestaurantRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val restaurantDao = db.restaurantDao()
    private val commentaireDao = db.commentaireDao()

    fun getAll() = restaurantDao.getAll()
    fun search(query: String) = restaurantDao.search(query)
    fun getById(id: Long) = restaurantDao.getById(id)
    fun getCommentaires(restaurantId: Long) = commentaireDao.getForRestaurant(restaurantId)

    suspend fun insert(restaurant: Restaurant) = restaurantDao.insert(restaurant)
    suspend fun update(restaurant: Restaurant) = restaurantDao.update(restaurant)
    suspend fun delete(restaurant: Restaurant) = restaurantDao.delete(restaurant)
    suspend fun addCommentaire(commentaire: Commentaire) = commentaireDao.insert(commentaire)
    suspend fun deleteCommentaire(commentaire: Commentaire) = commentaireDao.delete(commentaire)
}
