package com.carnet.restaurants.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.carnet.restaurants.R
import com.carnet.restaurants.adapter.CommentaireAdapter
import com.carnet.restaurants.databinding.FragmentDetailBinding
import com.carnet.restaurants.model.Restaurant
import com.carnet.restaurants.viewmodel.DetailViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private var currentRestaurant: Restaurant? = null
    private lateinit var commentaireAdapter: CommentaireAdapter

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            currentRestaurant?.let { r -> viewModel.updatePhoto(r, it.toString()) }
        }
    }

    private val pickCommentImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pendingCommentPhotoUri = it.toString()
            binding.imgCommentPreview.visibility = View.VISIBLE
            Glide.with(this).load(it).into(binding.imgCommentPreview)
        }
    }

    private var pendingCommentPhotoUri: String = ""
    private val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pickImageLauncher.launch("image/*")
        else Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.load(args.restaurantId)

        commentaireAdapter = CommentaireAdapter { commentaire ->
            viewModel.deleteCommentaire(commentaire)
        }
        binding.recyclerCommentaires.adapter = commentaireAdapter

        viewModel.restaurant.observe(viewLifecycleOwner) { resto ->
            if (resto == null) return@observe
            currentRestaurant = resto
            bindRestaurant(resto)
        }

        viewModel.commentaires.observe(viewLifecycleOwner) { list ->
            commentaireAdapter.submitList(list)
            binding.tvNoComments.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.imgRestaurant.setOnClickListener { requestPhotoPermission() }
        binding.btnChangerPhoto.setOnClickListener { requestPhotoPermission() }

        binding.tvAdresse.setOnClickListener {
            currentRestaurant?.adresse?.let { adresse ->
                if (adresse.isNotBlank()) openGoogleMaps(adresse)
            }
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) currentRestaurant?.let { viewModel.updateNotation(it, rating) }
        }

        binding.btnAjouterPhoto.setOnClickListener {
            pickCommentImageLauncher.launch("image/*")
        }

        binding.btnPublierComment.setOnClickListener {
            val texte = binding.etCommentaire.text.toString().trim()
            if (texte.isEmpty() && pendingCommentPhotoUri.isEmpty()) {
                Toast.makeText(requireContext(), "Écrivez un commentaire ou ajoutez une photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addCommentaire(args.restaurantId, texte, pendingCommentPhotoUri)
            binding.etCommentaire.text?.clear()
            pendingCommentPhotoUri = ""
            binding.imgCommentPreview.visibility = View.GONE
        }
    }

    private fun bindRestaurant(resto: Restaurant) {
        requireActivity().title = resto.nom
        binding.tvNom.text = resto.nom
        binding.tvAdresse.text = if (resto.adresse.isNotBlank()) "📍 ${resto.adresse}" else "Adresse non renseignée"
        binding.tvTelephone.text = if (resto.telephone.isNotBlank()) "📞 ${resto.telephone}" else "Téléphone non renseigné"
        binding.tvCuisine.text = if (resto.cuisine.isNotBlank()) "🍴 ${resto.cuisine}" else ""
        binding.tvNotes.text = resto.notes
        binding.ratingBar.rating = resto.notation

        if (resto.photoUri.isNotBlank()) {
            Glide.with(this).load(Uri.parse(resto.photoUri))
                .placeholder(R.drawable.ic_restaurant_placeholder)
                .into(binding.imgRestaurant)
        } else {
            binding.imgRestaurant.setImageResource(R.drawable.ic_restaurant_placeholder)
        }
    }

    private fun openGoogleMaps(adresse: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(adresse)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            val webUri = Uri.parse("https://maps.google.com/?q=${Uri.encode(adresse)}")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun requestPhotoPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permission)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val action = DetailFragmentDirections.actionDetailToForm(args.restaurantId)
                findNavController().navigate(action)
                true
            }
            R.id.action_delete -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Supprimer")
                    .setMessage("Supprimer ce restaurant définitivement ?")
                    .setPositiveButton("Supprimer") { _, _ ->
                        currentRestaurant?.let {
                            viewModel.delete(it)
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
