package com.example.live

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.live.databinding.FragmentBiometricBinding
import com.example.live.databinding.FragmentSuggestionsBinding
import com.google.firebase.analytics.FirebaseAnalytics

class BiometricsFragment : Fragment() {

    private var _binding: FragmentBiometricBinding? = null
    private val binding get() = _binding!!
    private lateinit var fbAnalytics: FirebaseAnalytics
    private var startTime : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBiometricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inizializza Firebase Analytics
        fbAnalytics = FirebaseAnalytics.getInstance(requireContext())
        binding.btnCalculate.setOnClickListener{
            calculateBiometrics()
        }
    }
    //Funzione per calcolare i dati biometrici
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun calculateBiometrics() {
        val selectedSexId = binding.radioGroupSex.checkedRadioButtonId
        val sex = if (selectedSexId == R.id.radioMale) "M" else if (selectedSexId == R.id.radioFemale) "F" else null
        val age = binding.editTextText.text.toString().toIntOrNull()
        val height = binding.editTextText2.text.toString().toIntOrNull()
        val weight = binding.editTextText3.text.toString().toIntOrNull()
        val laf = when (binding.spinnerLAF.selectedItemPosition) {
            0 -> 1.0
            1 -> 1.3
            2 -> 1.4
            3 -> 1.7
            4 -> 1.9
            else -> 1.0
        }

        if (age == null || height == null || weight == null || sex.isNullOrBlank() || age > 130 || height > 250 || weight > 500) {
            binding.tvResults.text = "Per favore, inserisci tutti i campi correttamente."
            return
        }

        val bmi = weight / ((height / 100.0) * (height / 100.0))
        val bmr = if (sex == "M") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
        val tdee = bmr * laf

        val bodyFatPercentage = if (sex == "M") {
            1.20 * bmi + 0.23 * age - 16.2
        } else {
            1.20 * bmi + 0.23 * age - 5.4
        }
        val leanBodyMass = weight * (1 - bodyFatPercentage / 100)
        val fatMass = weight - leanBodyMass

        binding.tvResults.text = "Indice di Massa Corporea (IMC): ${String.format("%.2f", bmi)} \nMassa Magra: ${String.format("%.2f", leanBodyMass)} kg \nMassa Grassa: ${String.format("%.2f", fatMass)} kg \nFabbisogno Energetico: ${String.format("%.2f", tdee)} kcal"

        showDetailsScreen(bmi)
    }
    //Funzione per visualizzare i suggerimenti in base all'IMC
    private fun showDetailsScreen(bmi : Double) {

        binding.fragmentContainer.removeAllViews()

        val suggestionsBinding = FragmentSuggestionsBinding.inflate(layoutInflater)

        val imageResource = when {
            bmi < 18.5 -> R.drawable.skinny
            bmi < 25 -> R.drawable.normal
            else -> R.drawable.overweight
        }
        suggestionsBinding.imageViewSuggestions.setImageResource(imageResource)
        suggestionsBinding.textViewSuggestions.text = when {
            bmi < 18.5 -> "Sei sottopeso. Dovresti aumentare il tuo apporto calorico."
            bmi < 25 -> "Il tuo peso è nella norma. Continua così!"
            else -> "Sei sovrappeso. Dovresti ridurre il tuo apporto calorico."
        }

        // Visualizzazione del messaggio
        binding.fragmentContainer.addView(suggestionsBinding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val timeSpent = System.currentTimeMillis() - startTime
        fragmentSwitchLog(timeSpent)
    }
    //Funzione per loggare i fragment switch
    private fun fragmentSwitchLog(time: Long) {
        val bundle = Bundle().apply {
            putLong("time", time)
        }
        fbAnalytics.logEvent("biometrics_fragment_switch", bundle)
    }
}
