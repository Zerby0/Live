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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.live.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedImageUri: Uri? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Carica l'URI dell'immagine salvato nelle SharedPreferences
        val uriString = sharedPreferences.getString("selected_image_uri", null)
        uriString?.let {
            selectedImageUri = Uri.parse(it)
            // Carica e mostra l'immagine
            loadCircularImage(selectedImageUri)
        }

        //Visualizzazione della mail dell'utente
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("user_email", "Email non disponibile")
        binding.textView12.text = userEmail

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                loadCircularImage(selectedImageUri)
                sharedPreferences.edit().putString("selected_image_uri", uri.toString()).apply()
            }
        }

        // Inizializza il ViewModel
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        // Calcola la media settimanale dei passi
        viewModel.calculateWeeklyAverageSteps()
        // Ottieni il giorno con meno passi
        viewModel.fetchDayWithLeastSteps()

        // Osserva il giorno con meno passi e aggiorna la TextView
        viewModel.dayWithLeastSteps.observe(viewLifecycleOwner, Observer { day ->
            if (day != null) {
                binding.lazyDay.text = day.date
                binding.lazySteps.text= day.steps.toString()
            } else {
                binding.lazyDay.text = "NaN"
                binding.lazySteps.text = "404"
            }
        })

        // Osserva la media settimanale dei passi e aggiorna la TextView
        viewModel.weeklyAverageSteps.observe(viewLifecycleOwner, Observer { averageSteps ->
            binding.averageWeekSteps.text = averageSteps.toString()
        })

        // Osserva il totale degli step e aggiorna la TextView
        viewModel.totalSteps.observe(viewLifecycleOwner) { totalSteps ->
            binding.totalStepsTextView.text = totalSteps?.toString() ?: "0"
        }

        binding.changeImageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        return binding.root
    }
    // Funzione per caricare l'immagine con forma circolare
    private fun loadCircularImage(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.profileImage)
    }
}