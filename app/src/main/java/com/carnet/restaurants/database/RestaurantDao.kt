package com.carnet.restaurants.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carnet.restaurants.model.Restaurant

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants ORDER BY dateAjout DESC")
    fun getAll(): LiveData<List<Restaurant>>

    @Query("""
        SELECT * FROM restaurants 
        WHERE nom LIKE '%' || :query || '%' 
        OR adresse LIKE '%' || :query || '%' 
        OR cuisine LIKE '%' || :query || '%'
        OR telephone LIKE '%' || :query || '%'
        ORDER BY dateAjout DESC
    """)
    fun search(query: String): LiveData<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getById(id: Long): LiveData<Restaurant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restaurant: Restaurant): Long

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)
}
