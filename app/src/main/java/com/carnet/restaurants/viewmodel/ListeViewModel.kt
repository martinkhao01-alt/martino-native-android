package com.carnet.restaurants.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.carnet.restaurants.model.Restaurant
import com.carnet.restaurants.repository.RestaurantRepository
import kotlinx.coroutines.launch

class ListeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = RestaurantRepository(application)
    private val searchQuery = MutableLiveData<String>("")

    val restaurants: LiveData<List<Restaurant>> = searchQuery.switchMap { query ->
        if (query.isBlank()) repo.getAll() else repo.search(query)
    }

    fun setSearch(query: String) { searchQuery.value = query }

    fun delete(restaurant: Restaurant) = viewModelScope.launch {
        repo.delete(restaurant)
    }
}
