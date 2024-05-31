package com.example.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.live.databinding.FragmentHomeBinding
import com.example.live.databinding.FragmentStatisticBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Puoi aggiungere logica per il tuo fragment qui
        binding.textStatistics.text = "Welcome to Statistics Fragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}