package com.example.live

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StepCountRepository(private val stepCountDao: StepCountDao) {

    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun uploadAllStepCounts() {
        withContext(Dispatchers.IO) {
            val stepCounts = stepCountDao.getAllStepCounts()
            for (stepCount in stepCounts) {
                val key = firebaseDatabase.child("step_counts").push().key
                if (key != null) {
                    firebaseDatabase.child("step_counts").child(key).setValue(stepCount)
                }
            }
        }
    }
}
