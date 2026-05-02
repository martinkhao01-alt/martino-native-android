package com.carnet.restaurants.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carnet.restaurants.model.Commentaire
import com.carnet.restaurants.model.Restaurant

@Database(
    entities = [Restaurant::class, Commentaire::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun commentaireDao(): CommentaireDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "carnet_restaurants.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
