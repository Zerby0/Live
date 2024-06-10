package com.example.live

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.live.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.analytics.FirebaseAnalytics

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    // Firebase
    private lateinit var fbAnalytics: FirebaseAnalytics
    private var startTime : Long = 0

    private var stepGoal = 10000
    private lateinit var viewModel: StepCountViewModel
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera il traguardo salvato
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        stepGoal = sharedPreferences.getInt("stepGoal", 10000)

        viewModel = ViewModelProvider(this)[StepCountViewModel::class.java]

        // Osserva i dati del conteggio passi
        viewModel.getStepCountForDate(currentDate).observe(viewLifecycleOwner) { stepCount ->
            // Aggiorna l'UI con i dati ricevuti
            if (stepCount != null) {
                updateStepCount(stepCount.steps, stepCount.calories)
            }
        }

        // Inizializza Firebase Analytics
        fbAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding.setGoalButton.setOnClickListener {
            val input = binding.goalInput.text.toString()
            if (input.isNotEmpty()) {
                stepGoal = input.toInt()
                sharedPreferences.edit().putInt("stepGoal", stepGoal).apply()
                updateProgressBar(binding.passiText.text.toString().substringAfter("Steps: ").toIntOrNull() ?: 0)
                Toast.makeText(requireContext(), "Goal updated to $stepGoal steps", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStepCount(stepCount: Int, calorieCount: Double) {
        binding.passiText.text = "Steps: $stepCount"
        binding.calorieBruciateText.text = "Calories: $calorieCount"
        updateProgressBar(stepCount)
        Log.e(TAG, "Stato dei passi updatato $stepCount")
    }

    private fun updateProgressBar(stepCount: Int) {
        val progressPercentage = (stepCount.toDouble() / stepGoal) * 100
        binding.progressBar.progress = progressPercentage.toInt().coerceAtMost(100)
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
    private fun fragmentSwitchLog(time: Long) {
        val bundle = Bundle().apply {
            putLong("time", time)
        }
        fbAnalytics.logEvent("home_fragment_switch", bundle)
    }
}