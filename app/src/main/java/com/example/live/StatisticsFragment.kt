package com.example.live

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.live.databinding.FragmentHomeBinding
import com.example.live.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StepCountViewModel by viewModels()
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAchievement.setOnClickListener {
            findNavController().navigate(R.id.action_statistic_to_achievement)
        }

        viewModel.getWeeklySteps().observe(viewLifecycleOwner, Observer { stepCounts ->
            val entries = ArrayList<BarEntry>()
            val dates = ArrayList<String>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            stepCounts.forEachIndexed { index, stepCount ->
                entries.add(BarEntry(index.toFloat(), stepCount.steps.toFloat()))
                dates.add(stepCount.date)
            }

            val barDataSet = BarDataSet(entries, "Numero di Passi")
            barDataSet.color = ContextCompat.getColor(requireContext(), R.color.purple_200)
            barDataSet.valueTextColor = Color.GRAY
            barDataSet.valueTextSize = 10f

            val data = BarData(barDataSet)
            binding.barChart.data = data

            val xAxis = binding.barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.valueFormatter = IndexAxisValueFormatter(dates)
            xAxis.textColor = Color.GRAY
            xAxis.textSize = 12f

            val leftAxis = binding.barChart.axisLeft
            leftAxis.setDrawGridLines(false)
            leftAxis.textColor = Color.GRAY
            leftAxis.textSize = 12f

            val rightAxis = binding.barChart.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.isEnabled = false

            binding.barChart.description.isEnabled = false
            binding.barChart.legend.isEnabled = false

            binding.barChart.animateY(1000)

            binding.barChart.invalidate()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun getStepsForWeekFrom(data: String): List<Int>{
        var steps = listOf(1)
        return steps
    }
}