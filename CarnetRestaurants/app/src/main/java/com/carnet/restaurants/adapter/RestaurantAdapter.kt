package com.carnet.restaurants.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carnet.restaurants.R
import com.carnet.restaurants.databinding.ItemRestaurantBinding
import com.carnet.restaurants.model.Restaurant

class RestaurantAdapter(
    private val onClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant) {
            binding.tvNom.text = restaurant.nom
            binding.tvAdresse.text = restaurant.adresse.ifBlank { "Adresse non renseignée" }
            binding.tvCuisine.text = restaurant.cuisine
            binding.ratingBar.rating = restaurant.notation
            binding.ratingBar.isEnabled = false

            if (restaurant.photoUri.isNotBlank()) {
                Glide.with(binding.root)
                    .load(Uri.parse(restaurant.photoUri))
                    .centerCrop()
                    .placeholder(R.drawable.ic_restaurant_placeholder)
                    .into(binding.imgRestaurant)
            } else {
                binding.imgRestaurant.setImageResource(R.drawable.ic_restaurant_placeholder)
            }

            binding.root.setOnClickListener { onClick(restaurant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(a: Restaurant, b: Restaurant) = a.id == b.id
            override fun areContentsTheSame(a: Restaurant, b: Restaurant) = a == b
        }
    }
}
