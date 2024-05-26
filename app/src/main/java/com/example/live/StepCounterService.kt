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
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0

    override fun onCreate() {
        super.onCreate()

        // Inizializza il SensorManager per ottenere i sensori di sistema
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Prendi il sensore di tipo STEP_COUNTER (contapassi)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Registra il listener per il sensore di contapassi se il sensore è disponibile
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Log.e("StepCounterService", "Step counter sensor not available!")
        }

        // Avvia il servizio in foreground
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "StepCounterServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Step Counter Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Step Counter Service")
            .setContentText("Counting your steps...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            // Aggiorna il contapassi
            stepCount = event.values[0].toInt()
            Log.d("StepCounterService", "Step count: $stepCount")

            // Invia i dati tramite broadcast locale ()
            val intent = Intent("StepCounterUpdate")
            intent.putExtra("stepCount", stepCount)
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
