package com.example.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class StepCountViewModel(private val stepCountDao: StepCountDao) : ViewModel() {


    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> get() = _achievements

    init {
        _achievements.value = listOf(
            Achievement("Primo Passo", "Completa 1.000 passi in un giorno.", listOf(1000, 1)),
            Achievement("Piccolo Camminatore", "Completa 5.000 passi in un giorno.", listOf(5000, 1)),
            Achievement("Costanza Iniziale", "Completa 5.000 passi al giorno per 3 giorni consecutivi.", listOf(5000, 3)),
            Achievement("Camminatore Quotidiano", "Completa 10.000 passi in un giorno.", listOf(10000, 1)),
            Achievement("Semplicemente Passeggiando", "Completa 50.000 passi in una settimana.", listOf(50000,7)),
            Achievement("Caminatore Determinato", "Completa 10.000 passi al giorno per 7 giorni consecutivi.", listOf(10000,1,7)),
            Achievement("Prima Pietra", "Completa 100.000 passi in totale.", listOf(100000)),
            Achievement("Settimana Perfetta", "Completa 70.000 passi in una settimana.", listOf(70000,7)),
            Achievement("Camminatore Mensile", "Completa 300.000 passi in un mese.", listOf(300000,30)),
            Achievement("Giorno di Maratona", "Completa 42.195 passi in un giorno (equivalente a una maratona).", listOf(42195,1)),
            Achievement("Camminatore Dedito", "Completa 10.000 passi al giorno per 30 giorni consecutivi.", listOf(10000,1,30)),
            Achievement("Milione di Passi", "Completa 1.000.000 di passi in totale.", listOf(1000000)),
            Achievement("Scalata delle Cime", "Completa 20.000 passi in un giorno.", listOf(20000,1)),
            Achievement("Doppia Maratona", "Completa 84.390 passi in un giorno (equivalente a due maratone).", listOf(84390,1)),
            Achievement("Camminatore dell'Anno", "Completa 3.650.000 passi in un anno.", listOf(3650000,365)),
            Achievement("Camminatore Impavido", "Completa 50.000 passi in un giorno.", listOf(50000,1)),
            Achievement("Devoto della Passeggiata", "Completa 10.000 passi al giorno per 100 giorni consecutivi.", listOf(10000,1,100)),
            Achievement("Dieci Milioni di Passi", "Completa 10.000.000 di passi in totale.", listOf(10000000)),
            Achievement("Superstar del Cammino", "Completa 100.000 passi in un giorno.", listOf(100000,1)),
            Achievement("Leggenda del Cammino", "Completa 50.000.000 di passi in totale.", listOf(50000000))
        )
    }

    fun updateAchievements(stepsToday: Int, totalSteps: Int, consecutiveDays: Int) {
        val updatedAchievements = _achievements.value?.map { achievement ->
            when {
                achievement.condition.size == 2 -> {
                    val stepsRequired = achievement.condition[0]
                    val daysRequired = achievement.condition[1]
                    if (stepsToday >= stepsRequired && consecutiveDays >= daysRequired) {
                        achievement.copy(isCompleted = true)
                    } else {
                        achievement
                    }
                }
                achievement.condition.size == 1 -> {
                    val stepsRequired = achievement.condition[0]
                    if (totalSteps >= stepsRequired) {
                        achievement.copy(isCompleted = true)
                    } else {
                        achievement
                    }
                }
                else -> achievement
            }
        }
        _achievements.value = updatedAchievements!!
    }

    // Funzione per ottenere il conteggio dei passi in modo sincrono
    suspend fun getStepCountForDateSync(date: String): StepCount? {
        return stepCountDao.getStepCountForDateSync(date)
    }

    // Funzione per ottenere il LiveData dei passi per osservare in modo asincrono
    fun getStepCountForDate(date: String): LiveData<StepCount>? {
        return stepCountDao.getStepCountForDate(date)
    }

    // Funzione per inserire i dati
    fun insertStepCount(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountDao.insert(stepCount)
        }
    }
    suspend fun getTotalSteps(): Int {
        return stepCountDao.getTotalSteps() ?: 0
    }
    suspend fun getConsecutiveDaysCount(startDate: String, endDate: String): Int {
        val daysWithMinimumSteps = stepCountDao.getDaysWithMinimumSteps()
        var consecutiveDaysCount = 0

        var currentDate = startDate
        while (currentDate <= endDate) {
            if (currentDate in daysWithMinimumSteps) {
                consecutiveDaysCount++
            } else {
                if (consecutiveDaysCount > 0) {
                    break
                }
            }
            currentDate = LocalDate.parse(currentDate).plusDays(1).toString()
        }

        return consecutiveDaysCount
    }
}