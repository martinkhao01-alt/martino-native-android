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
    private var prixSelectionne: Int = 0
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
        setupPrixSelector()

        if (isEdit) {
            viewModel.loadForEdit(args.restaurantId)
            viewModel.restaurant.observe(viewLifecycleOwner) { resto ->
                if (resto != null) {
                    binding.etNom.setText(resto.nom)
                    binding.etAdresse.setText(resto.adresse)
                    binding.etTelephone.setText(resto.telephone)
                    binding.etSiteWeb.setText(resto.siteWeb)
                    binding.etCuisine.setText(resto.cuisine)
                    binding.etNotes.setText(resto.notes)
                    binding.ratingBar.rating = resto.notation
                    photoUri = resto.photoUri
                    prixSelectionne = resto.prixFourchette
                    updatePrixUI(prixSelectionne)
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
            if (nom.isEmpty()) { binding.tilNom.error = "Le nom est obligatoire"; return@setOnClickListener }
            binding.tilNom.error = null

            var siteWeb = binding.etSiteWeb.text.toString().trim()
            if (siteWeb.isNotBlank() && !siteWeb.startsWith("http")) siteWeb = "https://$siteWeb"

            viewModel.save(
                nom = nom,
                adresse = binding.etAdresse.text.toString().trim(),
                telephone = binding.etTelephone.text.toString().trim(),
                siteWeb = siteWeb,
                cuisine = binding.etCuisine.text.toString().trim(),
                notes = binding.etNotes.text.toString().trim(),
                photoUri = photoUri,
                notation = binding.ratingBar.rating,
                prixFourchette = prixSelectionne
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

    private fun setupPrixSelector() {
        binding.btnPrix1.setOnClickListener { prixSelectionne = if (prixSelectionne == 1) 0 else 1; updatePrixUI(prixSelectionne) }
        binding.btnPrix2.setOnClickListener { prixSelectionne = if (prixSelectionne == 2) 0 else 2; updatePrixUI(prixSelectionne) }
        binding.btnPrix3.setOnClickListener { prixSelectionne = if (prixSelectionne == 3) 0 else 3; updatePrixUI(prixSelectionne) }
    }

    private fun updatePrixUI(selected: Int) {
        val ctx = requireContext()
        val activeColor = ctx.getColor(R.color.colorPrimary)
        val inactiveColor = ctx.getColor(R.color.prixInactive)
        val activeText = 0xFFFFFFFF.toInt()
        val inactiveText = ctx.getColor(R.color.prixInactiveText)
        listOf(Triple(binding.btnPrix1, 1, "€  Pas cher"),
               Triple(binding.btnPrix2, 2, "€€  Bon prix"),
               Triple(binding.btnPrix3, 3, "€€€  Très cher")).forEach { (btn, v, lbl) ->
            btn.text = lbl
            btn.setBackgroundColor(if (selected == v) activeColor else inactiveColor)
            btn.setTextColor(if (selected == v) activeText else inactiveText)
        }
    }

    private fun requestPhotoPermission() {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED)
            pickImageLauncher.launch("image/*")
        else permissionLauncher.launch(perm)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
