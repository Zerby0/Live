package com.example.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.live.databinding.FragmentHomeBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlin.math.round

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // Firebase
    private lateinit var fbAnalytics: FirebaseAnalytics
    private var startTime : Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera i dati dall'attività
        val activity = activity as MainActivity
        val stepCount = activity.stepCount
        val calorieCount = round(activity.calorieCount)

        // Inizializza Firebase Analytics
        fbAnalytics = FirebaseAnalytics.getInstance(requireContext())

        // Imposta i dati nei TextView utilizzando il binding
        binding.passiText.text = "Steps: $stepCount"
        binding.calorieBruciateText.text = "Calories: $calorieCount"
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
            putString("fragment", "HomeFragment")
            putLong("time", time)
        }
        fbAnalytics.logEvent("fragment_switch_main_activity", bundle)
    }

}