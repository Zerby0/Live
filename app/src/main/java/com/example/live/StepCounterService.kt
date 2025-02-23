package com.example.live

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0
    private var initialStepCount = -1
    private var globalDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate() {
        super.onCreate()

        // Inizializza il SensorManager per ottenere i sensori di sistema
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Prende il sensore di tipo STEP_COUNTER (contapassi)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        startForegroundService()

        // Registra il listener per il sensore di contapassi se il sensore è disponibile
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Log.e("StepCounterService", "Sensore contapassi non disponibile!")
            stopSelf() // Ferma il servizio se il sensore non è disponibile
        }

    }

    // Con START_STICKY se l'attività termina cercherà di riavviarsi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    // Serve per lasciare il servizio in esecuzione anche se l'app non è aperta
    private fun startForegroundService() {
        // Creazione del canale di notifica (per Android 8.0+)
        val channelId = "StepCounterServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Step Counter Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        // Mostra il messaggio di notifica del servizio in esecuzione
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("LIVE")
            .setContentText("Contando i tuoi passi...")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }
    // Funzione chiamata quando i dati del sensore cambiano
    override fun onSensorChanged(event: SensorEvent?) {
        //Prende la data di oggi e la confronta con la data globale che non si aggiorna se cambio data dalle impostazioni
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if(currentDate != globalDate){
            initialStepCount = stepCount
            globalDate = currentDate
        }
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepCount = event.values[0].toInt()
            if (initialStepCount < 0) {
                initialStepCount = totalStepCount
            }
            stepCount = totalStepCount - initialStepCount

            val liveDatabase = LiveDatabase.getDatabase(this)
            val stepCountDao = liveDatabase.stepCountDao()

            // Avvio di una coroutine per inserire o aggiornare i dati nel database
            CoroutineScope(Dispatchers.IO).launch {
                val existingStepCountLiveData = stepCountDao.getStepCountForDate(currentDate)
                val existingStepCount = existingStepCountLiveData.value
                if (existingStepCount != null) {
                    existingStepCount.steps += stepCount
                    stepCountDao.insert(existingStepCount)
                } else {
                    val stepCountObj = StepCount(date = currentDate, steps = stepCount)
                    stepCountDao.insert(stepCountObj)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       //Non è necessario implementare cambiamenti di accuratezza
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}