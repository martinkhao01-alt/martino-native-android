package com.carnet.restaurants.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.carnet.restaurants.model.Commentaire
import com.carnet.restaurants.model.Restaurant
import com.carnet.restaurants.repository.RestaurantRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = RestaurantRepository(application)
    private val _restaurantId = MutableLiveData<Long>()

    val restaurant: LiveData<Restaurant> = _restaurantId.switchMap { repo.getById(it) }
    val commentaires = _restaurantId.switchMap { repo.getCommentaires(it) }

    fun load(id: Long) { _restaurantId.value = id }

    fun updateNotation(restaurant: Restaurant, note: Float) = viewModelScope.launch {
        repo.update(restaurant.copy(notation = note))
    }

    fun updatePhoto(restaurant: Restaurant, uri: String) = viewModelScope.launch {
        repo.update(restaurant.copy(photoUri = uri))
    }

    fun addCommentaire(restaurantId: Long, texte: String, photoUri: String = "") = viewModelScope.launch {
        repo.addCommentaire(Commentaire(restaurantId = restaurantId, texte = texte, photoUri = photoUri))
    }

    fun deleteCommentaire(commentaire: Commentaire) = viewModelScope.launch {
        repo.deleteCommentaire(commentaire)
    }

    fun delete(restaurant: Restaurant) = viewModelScope.launch {
        repo.delete(restaurant)
    }
}
