package com.example.live
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        imageView = view.findViewById(R.id.profile_image)

        // Inizializza SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Carica l'URI dell'immagine salvato
        val uriString = sharedPreferences.getString("selected_image_uri", null)
        uriString?.let { uriString ->
            selectedImageUri = Uri.parse(uriString)
            // Carica e mostra l'immagine
            loadCircularImage(selectedImageUri)
        }

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

        view.findViewById<View>(R.id.change_image_button).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        return view
    }

    private fun loadCircularImage(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(imageView)
    }
}