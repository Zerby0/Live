package com.example.live
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.live.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedImageUri: Uri? = null
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Inizializza SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Carica l'URI dell'immagine salvato
        val uriString = sharedPreferences.getString("selected_image_uri", null)
        uriString?.let {
            selectedImageUri = Uri.parse(it)
            // Carica e mostra l'immagine
            loadCircularImage(selectedImageUri)
        }

        //Per la visualizzazione della mail di login
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("user_email", "Email non disponibile")
        binding.textView12.text = userEmail

        // Definisci pickMedia all'interno di onCreateView
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                // Carica e mostra l'immagine
                loadCircularImage(selectedImageUri)
                // Salva l'URI dell'immagine selezionata quando l'applicazione viene chiusa
                sharedPreferences.edit().putString("selected_image_uri", uri.toString()).apply()
            }
        }

        binding.changeImageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        return binding.root
    }

    private fun loadCircularImage(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.profileImage)
    }
}