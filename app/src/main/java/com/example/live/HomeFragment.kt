package com.example.live

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.live.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var stepGoal = 10000
    private lateinit var stepCountViewModel: StepCountViewModel
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


    fun updateStepCount(stepCount: Int, calorieCount: Double) {
        binding.passiText.text = "Steps: $stepCount"
        binding.calorieBruciateText.text = "Calories: $calorieCount"
        updateProgressBar(stepCount)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val stepCountDao = LiveDatabaseInitializer.getInstance().stepCountDao()
        val viewModelFactory = StepCountViewModelFactory(stepCountDao)
        stepCountViewModel = ViewModelProvider(this, viewModelFactory).get(StepCountViewModel::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            val stepCountLiveData = withContext(Dispatchers.IO) {
                stepCountViewModel.getStepCountForDateSync(currentDate)
            }
            // Osserva i dati nel LiveData
            stepCountLiveData?.observe(viewLifecycleOwner) { stepCount ->
                stepCount?.let {
                    updateStepCount(stepCount.steps, stepCount.calories)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera il traguardo salvato
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        stepGoal = sharedPreferences.getInt("stepGoal", 10000)

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
    //stepCount messo Double per aver maggiore precisione, coerceAtMost per non superare il 100%
    private fun updateProgressBar(stepCount: Int) {
        val progressPercentage = (stepCount.toDouble() / stepGoal) * 100
        binding.progressBar.progress = progressPercentage.toInt().coerceAtMost(100)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
