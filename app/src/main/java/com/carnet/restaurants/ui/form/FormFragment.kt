package com.carnet.restaurants.ui.form

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.carnet.restaurants.R
import com.carnet.restaurants.databinding.FragmentFormBinding
import com.carnet.restaurants.viewmodel.FormViewModel

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private val args: FormFragmentArgs by navArgs()
    private val viewModel: FormViewModel by viewModels()
    private var photoUri: String = ""
    private var isEdit = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            photoUri = it.toString()
            Glide.with(this).load(it).centerCrop().into(binding.imgPhoto)
            binding.tvAjouterPhoto.text = "Changer la photo"
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pickImageLauncher.launch("image/*")
        else Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isEdit = args.restaurantId != 0L
        requireActivity().title = if (isEdit) "Modifier" else "Nouveau restaurant"

        if (isEdit) {
            viewModel.loadForEdit(args.restaurantId)
            viewModel.restaurant.observe(viewLifecycleOwner) { resto ->
                if (resto != null) {
                    binding.etNom.setText(resto.nom)
                    binding.etAdresse.setText(resto.adresse)
                    binding.etTelephone.setText(resto.telephone)
                    binding.etCuisine.setText(resto.cuisine)
                    binding.etNotes.setText(resto.notes)
                    binding.ratingBar.rating = resto.notation
                    photoUri = resto.photoUri
                    if (resto.photoUri.isNotBlank()) {
                        Glide.with(this).load(Uri.parse(resto.photoUri)).centerCrop().into(binding.imgPhoto)
                        binding.tvAjouterPhoto.text = "Changer la photo"
                    }
                }
            }
        }

        binding.cardPhoto.setOnClickListener { requestPhotoPermission() }

        binding.btnEnregistrer.setOnClickListener {
            val nom = binding.etNom.text.toString().trim()
            if (nom.isEmpty()) {
                binding.tilNom.error = "Le nom est obligatoire"
                return@setOnClickListener
            }
            binding.tilNom.error = null

            viewModel.save(
                nom = nom,
                adresse = binding.etAdresse.text.toString().trim(),
                telephone = binding.etTelephone.text.toString().trim(),
                cuisine = binding.etCuisine.text.toString().trim(),
                notes = binding.etNotes.text.toString().trim(),
                photoUri = photoUri,
                notation = binding.ratingBar.rating
            ) { savedId ->
                requireActivity().runOnUiThread {
                    if (isEdit) findNavController().popBackStack()
                    else {
                        val action = FormFragmentDirections.actionFormToDetail(savedId)
                        findNavController().navigate(action)
                    }
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
