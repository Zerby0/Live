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
    private lateinit var recyclerView: RecyclerView
    private lateinit var stepCountViewModel: StepCountViewModel
    private lateinit var achievementsAdapter: AchievementAdapter
    private var _binding: FragmentItemListBinding? = null
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)


        val stepCountDao = LiveDatabaseInitializer.getInstance(requireContext()).stepCountDao()
        val viewModelFactory = StepCountViewModelFactory(stepCountDao)
        stepCountViewModel = ViewModelProvider(this, viewModelFactory).get(StepCountViewModel::class.java)

        stepCountViewModel.achievements.observe(viewLifecycleOwner) { achievements ->
            // Aggiorna il tuo RecyclerView con la lista di achievements
            updateRecyclerView(achievements)
        }

        // Esegui una coroutine per caricare i dati iniziali
        viewLifecycleOwner.lifecycleScope.launch {
            val stepCount = withContext(Dispatchers.IO) {
                stepCountViewModel.getStepCountForDateSync(currentDate)
            }
            val totalSteps = stepCountViewModel.getTotalSteps()
            val consecutiveDays = stepCountViewModel.getConsecutiveDaysCount("2024-06-03", currentDate)
            stepCount?.let {
                stepCountViewModel.updateAchievements(it.steps, totalSteps, consecutiveDays)
            }
        }

        // Configura l'adapter
        achievementsAdapter = AchievementAdapter(emptyList())
        binding.recyclerView.adapter = achievementsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Osserva i cambiamenti negli achievements
        stepCountViewModel.achievements.observe(viewLifecycleOwner) { achievements ->
            achievementsAdapter.updateAchievements(achievements)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Trova il RecyclerView nel layout
        recyclerView = view.findViewById(R.id.recyclerView)

    }

    //TODO
    override fun onPause() {
        super.onPause()
        findNavController().popBackStack(R.id.itemListFragment, false)
    }

    private fun updateRecyclerView(achievements: List<Achievement>) {
        adapter = AchievementAdapter(achievements)
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}

