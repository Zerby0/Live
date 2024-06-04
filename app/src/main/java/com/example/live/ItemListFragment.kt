package com.example.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.live.databinding.FragmentItemListBinding
import com.example.live.databinding.FragmentStatisticBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemListFragment : Fragment() {

    private lateinit var adapter: AchievementAdapter
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val achievements = mutableListOf(
        Achievement("Primo Passo", "Completa 1.000 passi in un giorno.", listOf(1000, 1)),
        Achievement("Piccolo Camminatore", "Completa 5.000 passi in un giorno.", listOf(5000, 1)),
        Achievement("Costanza Iniziale", "Completa 5.000 passi al giorno per 3 giorni consecutivi.", listOf(5000, 3)),
        Achievement("Camminatore Quotidiano", "Completa 10.000 passi in un giorno.", listOf(10000, 1)),
        Achievement("Semplicemente Passeggiando", "Completa 50.000 passi in una settimana.", listOf(50000, 7)),
        Achievement("Caminatore Determinato", "Completa 10.000 passi al giorno per 7 giorni consecutivi.", listOf(10000, 1, 7)),
        Achievement("Prima Pietra", "Completa 100.000 passi in totale.", listOf(100000)),
        Achievement("Settimana Perfetta", "Completa 70.000 passi in una settimana.", listOf(70000, 7)),
        Achievement("Camminatore Mensile", "Completa 300.000 passi in un mese.", listOf(300000, 30)),
        Achievement("Giorno di Maratona", "Completa 42.195 passi in un giorno (equivalente a una maratona).", listOf(42195, 1)),
        Achievement("Camminatore Dedito", "Completa 10.000 passi al giorno per 30 giorni consecutivi.", listOf(10000, 1, 30)),
        Achievement("Milione di Passi", "Completa 1.000.000 di passi in totale.", listOf(1000000)),
        Achievement("Scalata delle Cime", "Completa 20.000 passi in un giorno.", listOf(20000, 1)),
        Achievement("Doppia Maratona", "Completa 84.390 passi in un giorno (equivalente a due maratone).", listOf(84390, 1)),
        Achievement("Camminatore dell'Anno", "Completa 3.650.000 passi in un anno.", listOf(3650000, 365)),
        Achievement("Camminatore Impavido", "Completa 50.000 passi in un giorno.", listOf(50000, 1)),
        Achievement("Devoto della Passeggiata", "Completa 10.000 passi al giorno per 100 giorni consecutivi.", listOf(10000, 1, 100)),
        Achievement("Dieci Milioni di Passi", "Completa 10.000.000 di passi in totale.", listOf(10000000)),
        Achievement("Superstar del Cammino", "Completa 100.000 passi in un giorno.", listOf(100000, 1)),
        Achievement("Leggenda del Cammino", "Completa 50.000.000 di passi in totale.", listOf(50000000))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        // Configura l'adapter con la lista di achievement
        adapter = AchievementAdapter(achievements)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Esegui il controllo degli achievement
        checkAchievements()
    }

    private fun checkAchievements() {
        // Supponiamo che tu abbia un ViewModel con i dati dei passi
        val viewModel = ViewModelProvider(this).get(StepCountViewModel::class.java)
        viewModel.getStepCountForDate(currentDate)?.observe(viewLifecycleOwner) { stepCount ->
            if (stepCount != null) {
                for (achievement in achievements) {
                    if (!achievement.isCompleted) {
                        if (isAchievementUnlocked(achievement, stepCount.steps)) {
                            achievement.isCompleted = true
                            // Puoi anche salvare lo stato sbloccato nel database se necessario
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun isAchievementUnlocked(achievement: Achievement, steps: Int): Boolean {
        // Esegui il controllo delle condizioni
        val requiredSteps = achievement.condition[0]
        return steps >= requiredSteps
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
