package com.example.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.live.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


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
        val calorieCount = activity.calorieCount

        // Imposta i dati nei TextView utilizzando il binding
        binding.passiText.text = "Steps: $stepCount"
        binding.calorieBruciateText.text = "Calories: $calorieCount"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}