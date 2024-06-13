package com.example.live

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StepCountRepository
    private val allStepCounts: LiveData<List<StepCount>>
    private val achievementDao = LiveDatabase.getDatabase(application).achievementDao()
    val allAchievements: LiveData<List<Achievement>> = achievementDao.getAll()
    val totalSteps: LiveData<Int?>
    val weeklyAverageSteps = MutableLiveData<Int>()
    val dayWithLeastSteps = MutableLiveData<StepCount?>()

    init {
        val stepCountDao = LiveDatabase.getDatabase(application).stepCountDao()
        repository = StepCountRepository(stepCountDao)
        allStepCounts = repository.getAllStepCounts()
        totalSteps =  repository.getTotalSteps()
    }

    fun getStepCountForDate(date: String): LiveData<StepCount?> {
        return repository.getStepCountForDate(date)
    }

    fun getStepHistory(): LiveData<List<StepCount>> {
        return repository.getStepHistory()
    }

    // Funzione per controllare l'avanzamento degli achievement
    fun updateAchievement(achievement: Achievement) {
        viewModelScope.launch {
            achievementDao.update(achievement)
        }
    }
    //Creazione degli achievement
    fun initializeAchievements() {
        viewModelScope.launch {
            val achievement = achievementDao.getAchievementByTitleSync("Ma Allora Cammini!")
            if (achievement == null) {
                Log.e(TAG, "Achievement resettati")
                val achievements = listOf(
                    Achievement("Ma Allora Cammini!", "Fai il tuo primo passo, benvenuto.", listOf(1), false),
                    Achievement("Belli!!!", "Fai 50 passi in un giorno", listOf(50,1), false),
                    Achievement("Alzati dal divano", "Fai 75 passi in un giorno", listOf(75,1), false),
                    Achievement("Primo Passo", "Completa 1.000 passi in un giorno.", listOf(1000,1), false),
                    Achievement("Piccolo Camminatore", "Completa 5.000 passi in un giorno.", listOf(5000,1), false),
                    Achievement("Costanza Iniziale", "Completa 5.000 passi al giorno per 3 giorni consecutivi.", listOf(5000,3), false),
                    Achievement("Camminatore Quotidiano", "Completa 10.000 passi in un giorno.", listOf(10000,1), false),
                    Achievement("Semplicemente Passeggiando", "Completa 50.000 passi in una settimana.", listOf(50000,7), false),
                    Achievement("Caminatore Determinato", "Completa 10.000 passi al giorno per 7 giorni consecutivi.", listOf(10000,1,7), false),
                    Achievement("Prima Pietra", "Completa 100.000 passi in totale.", listOf(100000), false),
                    Achievement("Settimana Perfetta", "Completa 70.000 passi in una settimana.", listOf(70000,7), false),
                    Achievement("Camminatore Mensile", "Completa 300.000 passi in un mese.", listOf(300000,30), false),
                    Achievement("Giorno di Maratona", "Completa 42.195 passi in un giorno (equivalente a una maratona).", listOf(42195,1), false),
                    Achievement("Camminatore Dedito", "Completa 10.000 passi al giorno per 30 giorni consecutivi.", listOf(10000,1,30), false),
                    Achievement("Milione di Passi", "Completa 1.000.000 di passi in totale.", listOf(1000000), false),
                    Achievement("Scalata delle Cime", "Completa 20.000 passi in un giorno.", listOf(20000,1), false),
                    Achievement("Doppia Maratona", "Completa 84.390 passi in un giorno (equivalente a due maratone).", listOf(84390,1), false),
                    Achievement("Camminatore dell'Anno", "Completa 3.650.000 passi in un anno.", listOf(3650000,365), false),
                    Achievement("Camminatore Impavido", "Completa 50.000 passi in un giorno.", listOf(50000,1), false),
                    Achievement("Devoto della Passeggiata", "Completa 10.000 passi al giorno per 100 giorni consecutivi.", listOf(10000,1,100), false),
                    Achievement("Dieci Milioni di Passi", "Completa 10.000.000 di passi in totale.", listOf(10000000), false),
                    Achievement("Superstar del Cammino", "Completa 100.000 passi in un giorno.", listOf(100000,1), false),
                    Achievement("Leggenda del Cammino", "Completa 50.000.000 di passi in totale.", listOf(50000000), false)
                )
                achievementDao.insertAll(*achievements.toTypedArray())
            }
        }
    }

    fun calculateWeeklyAverageSteps() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val endDate = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startDate = sdf.format(calendar.time)

            val steps = repository.getWeeklySteps(startDate, endDate)
            val totalSteps = steps.sumOf { it.steps }
            val averageSteps = if (steps.isNotEmpty()) totalSteps / 7.0 else 0.0
            weeklyAverageSteps.postValue(averageSteps.toInt())
        }
    }

    fun getWeeklySteps(): LiveData<List<StepCount>> {
        val weeklySteps = MutableLiveData<List<StepCount>>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()

        for (i in 0 until 7) {
            val date = sdf.format(calendar.time)
            dates.add(date)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        viewModelScope.launch {
            val steps = repository.getStepCountsForDates(dates)
            weeklySteps.postValue(steps)
        }

        return weeklySteps
    }

    fun fetchDayWithLeastSteps() {
        viewModelScope.launch {
            val day = repository.getDayWithLeastSteps()
            dayWithLeastSteps.postValue(day)
        }
    }
}