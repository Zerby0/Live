package com.example.live

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.live.databinding.FragmentItemListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemListFragment : Fragment() {

    private lateinit var adapter: AchievementAdapter
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        // Inizializza gli achievement
        viewModel.initializeAchievements()

        // Configura l'adapter con la lista di achievement
        adapter = AchievementAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allAchievements.observe(viewLifecycleOwner) { achievementEntities ->
            val achievements = achievementEntities.map { entity ->
                Achievement(entity.title, entity.description, entity.condition, entity.isCompleted)
            }
            adapter.setAchievements(achievements)
            checkAchievements(achievements)
        }
    }
    // Funzione per controllare l'avanzamento degli achievement
    private fun checkAchievements(achievements: List<Achievement>) {
        viewModel.getStepCountForDate(currentDate).observe(viewLifecycleOwner) { stepCount ->
            stepCount?.let { currentStepCount ->
                viewModel.getStepHistory().observe(viewLifecycleOwner) { stepHistory ->
                    val achievementsToUpdate = achievements.filter { !it.isCompleted && isAchievementUnlocked(it, currentStepCount.steps, stepHistory) }
                    if (achievementsToUpdate.isNotEmpty()) {
                        achievementsToUpdate.forEach { achievement ->
                            achievement.isCompleted = true
                            viewModel.updateAchievement(achievement)
                        }
                        adapter.setAchievements(achievements)
                    }
                }
            }
        }
    }
    // Funzione per controllare se un achievement è stato sbloccato
    private fun isAchievementUnlocked(achievement: Achievement, steps: Int, stepHistory: List<StepCount>): Boolean {
        val requiredSteps = achievement.condition[0]
        Log.e(TAG, achievement.title)
        if(achievement.isCompleted) return true

        return when (achievement.condition.size) {
            1 -> { // achievement basato su passi totali
                Log.e(TAG, "Numero di Condizioni ${achievement.condition.size}  Passi richiesti $requiredSteps")
                steps >= requiredSteps
            }
            2 -> { // achievement basato su passi giornalieri
                val requiredDays = achievement.condition[1]
                Log.e(TAG, "Numero di Condizioni ${achievement.condition.size}  Passi richiesti $requiredSteps Numero di giorni: $requiredDays")
                Log.e(TAG, "Giorno in questione: ${stepHistory.takeLast(requiredDays).map { it.date }}")
                stepHistory.takeLast(requiredDays).all { it.steps >= requiredSteps }
            }
            3 -> { // achievement basato su passi giornalieri consecutivi
                val requiredDays = achievement.condition[2]
                val dailySteps = stepHistory.takeLast(requiredDays)
                if (dailySteps.size < requiredDays) {
                    false
                } else {
                    dailySteps.all { it.steps >= requiredSteps }
                }
            }
            else -> false
        }
    }

    override fun onPause() {
        super.onPause()
        findNavController().popBackStack(R.id.itemListFragment, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
