package com.example.live

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0
    private var calorieCount = 0.0
    private var initialStepCount = -1
    private val caloriesPerStep  = 0.04
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // Inizializza il SensorManager per ottenere i sensori di sistema
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Prendi il sensore di tipo STEP_COUNTER (contapassi)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Avvia il servizio in foreground
        startForegroundService()

        // Inizializza SharedPreferences
        sharedPreferences = getSharedPreferences("stepCounterPrefs", Context.MODE_PRIVATE)
        stepCount = sharedPreferences.getInt("stepCount", 0)

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

        // Fa il messaggio di notifica che dice che conta i passi
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servizio di StepCounter")
            .setContentText("Contando i tuoi passi: $stepCount")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepCount = event.values[0].toInt()
            if (initialStepCount < 0) {
                initialStepCount = totalStepCount
            }
            stepCount = totalStepCount - initialStepCount
            calorieCount = stepCount * caloriesPerStep
            Log.d("StepCounterService", "Conteggio passi: $stepCount")

            // Salva il conteggio dei passi in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt("stepCount", stepCount)
            editor.putFloat("calorieCount", calorieCount.toFloat())
            editor.apply()

            // Invia i dati tramite broadcast locale
            val intent = Intent("StepCounterUpdate")
            intent.putExtra("stepCount", stepCount)
            intent.putExtra("calorieCount", calorieCount)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Gestisci i cambiamenti di accuratezza se necessario
    }

    override fun onDestroy() {
        super.onDestroy()
        // Deregistra il listener del sensore per risparmiare energia
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}