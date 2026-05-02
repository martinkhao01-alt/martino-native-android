package com.carnet.restaurants.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.carnet.restaurants.model.Restaurant
import com.carnet.restaurants.repository.RestaurantRepository
import kotlinx.coroutines.launch

class FormViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = RestaurantRepository(application)

    val restaurant: LiveData<Restaurant?> = MutableLiveData(null)
    private var editId: Long = 0L

    fun loadForEdit(id: Long) {
        editId = id
        (restaurant as MutableLiveData).value = null
        viewModelScope.launch {
            repo.getById(id).observeForever { r ->
                if (r != null && (restaurant as MutableLiveData).value == null) {
                    (restaurant as MutableLiveData).value = r
                }
            }
        }
    }

    fun save(
        nom: String, adresse: String, telephone: String,
        cuisine: String, notes: String, photoUri: String, notation: Float,
        onDone: (Long) -> Unit
    ) = viewModelScope.launch {
        val id = if (editId == 0L) {
            repo.insert(Restaurant(nom = nom, adresse = adresse, telephone = telephone,
                cuisine = cuisine, notes = notes, photoUri = photoUri, notation = notation))
        } else {
            repo.update(Restaurant(id = editId, nom = nom, adresse = adresse,
                telephone = telephone, cuisine = cuisine, notes = notes,
                photoUri = photoUri, notation = notation))
            editId
        }
        onDone(id)
    }
}
