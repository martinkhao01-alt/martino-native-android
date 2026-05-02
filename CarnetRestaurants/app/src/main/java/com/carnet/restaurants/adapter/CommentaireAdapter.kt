package com.carnet.restaurants.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carnet.restaurants.databinding.ItemCommentaireBinding
import com.carnet.restaurants.model.Commentaire
import java.text.SimpleDateFormat
import java.util.*

class CommentaireAdapter(
    private val onDelete: (Commentaire) -> Unit
) : ListAdapter<Commentaire, CommentaireAdapter.ViewHolder>(DIFF) {

    private val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)

    inner class ViewHolder(private val binding: ItemCommentaireBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(commentaire: Commentaire) {
            binding.tvTexte.text = commentaire.texte
            binding.tvDate.text = sdf.format(Date(commentaire.date))

            if (commentaire.photoUri.isNotBlank()) {
                binding.imgCommentaire.visibility = View.VISIBLE
                Glide.with(binding.root)
                    .load(Uri.parse(commentaire.photoUri))
                    .centerCrop()
                    .into(binding.imgCommentaire)
            } else {
                binding.imgCommentaire.visibility = View.GONE
            }

            binding.btnSupprimer.setOnClickListener { onDelete(commentaire) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentaireBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Commentaire>() {
            override fun areItemsTheSame(a: Commentaire, b: Commentaire) = a.id == b.id
            override fun areContentsTheSame(a: Commentaire, b: Commentaire) = a == b
        }
    }
}
