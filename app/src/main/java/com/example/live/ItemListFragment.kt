package com.example.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.live.databinding.FragmentItemListBinding
import com.example.live.databinding.FragmentStatisticBinding

class ItemListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AchievementAdapter
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Trova il RecyclerView nel layout
        recyclerView = view.findViewById(R.id.recyclerView)

        // Inizializza i dati (in un'app reale, questi potrebbero venire da un database o un'API)
        val achievements = listOf(
            Achievement("Primo Passo", "Completa 1.000 passi in un giorno."),
            Achievement("Piccolo Camminatore", "Completa 5.000 passi in un giorno."),
            Achievement("Costanza Iniziale", "Completa 5.000 passi al giorno per 3 giorni consecutivi."),
            Achievement("Camminatore Quotidiano", "Completa 10.000 passi in un giorno."),
            Achievement("Semplicemente Passeggiando", "Completa 50.000 passi in una settimana."),
            Achievement("Caminatore Determinato", "Completa 10.000 passi al giorno per 7 giorni consecutivi."),
            Achievement("Prima Pietra", "Completa 100.000 passi in totale."),
            Achievement("Settimana Perfetta", "Completa 70.000 passi in una settimana."),
            Achievement("Camminatore Mensile", "Completa 300.000 passi in un mese."),
            Achievement("Giorno di Maratona", "Completa 42.195 passi in un giorno (equivalente a una maratona)."),
            Achievement("Camminatore Dedito", "Completa 10.000 passi al giorno per 30 giorni consecutivi."),
            Achievement("Milione di Passi", "Completa 1.000.000 di passi in totale."),
            Achievement("Scalata delle Cime", "Completa 20.000 passi in un giorno."),
            Achievement("Doppia Maratona", "Completa 84.390 passi in un giorno (equivalente a due maratone)."),
            Achievement("Camminatore dell'Anno", "Completa 3.650.000 passi in un anno."),
            Achievement("Camminatore Impavido", "Completa 50.000 passi in un giorno."),
            Achievement("Devoto della Passeggiata", "Completa 10.000 passi al giorno per 100 giorni consecutivi."),
            Achievement("Dieci Milioni di Passi", "Completa 10.000.000 di passi in totale."),
            Achievement("Superstar del Cammino", "Completa 100.000 passi in un giorno."),
            Achievement("Leggenda del Cammino", "Completa 50.000.000 di passi in totale.")
        )

        // Imposta l'adapter per il RecyclerView
        adapter = AchievementAdapter(achievements)
        recyclerView.adapter = adapter
        // Imposta il layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    //TODO
    override fun onPause() {
        super.onPause()
        findNavController().popBackStack(R.id.itemListFragment, false)
    }
}

