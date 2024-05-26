package com.example.live

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.logrocket.core.SDK
import android.content.Intent
import android.widget.Button
import android.Manifest
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.IntentFilter
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {

    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    private lateinit var stepCountTextView: TextView
    //Broadcast receiver per i passi contati
    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stepCount = intent?.getIntExtra("stepCount", 0) ?: 0
            Log.d("MainActivity", "Received step count: $stepCount")
            stepCountTextView.text = "Steps: $stepCount"
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        userData["name"] = "Tommy"
        userData["email"] = "gmail@gmail.com"
        userData["subscriptionPlan"] = "premium"

        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

        val statistics = findViewById<Button>(R.id.statistiche_settimanali_button)
        statistics.setOnClickListener {
            val myIntent = Intent(it.context, StatisticsActivity::class.java)
            startActivity(myIntent)
        }
        val biometrics = findViewById<Button>(R.id.dati_biometrici_button)
        biometrics.setOnClickListener {
            val myIntent = Intent(it.context, BiometricsActivity::class.java)
            startActivity(myIntent)
        }

        //Variabile da cambiare per conta passi
        stepCountTextView = findViewById(R.id.passi_text)

        //Verifica i permessi a run time in base alla versione Android (dalla <=10 non serve, quindi non controlla)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_RECOGNITION_REQUEST_CODE)
            } else {
                startStepCounterService()
            }
        } else {
            startStepCounterService()
        }
        //Prende i dati broadcast
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(stepCountReceiver, IntentFilter("StepCounterUpdate"))
    }

    override fun onDestroy() {
        super.onDestroy()
        //Disattiva il BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepCountReceiver)
    }

    //Starta il servizio
    private fun startStepCounterService() {
        val intent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    //Controlla se la richiesta di permessi è stata effettuata per il codice ACTIVITY_RECOGNITION_REQUEST_CODE,
    //Se la richiesta di permessi ha avuto successo (cioè grantResults[0] è uguale a PackageManager.PERMISSION_GRANTED),
    //allora il servizio di conteggio dei passi viene avviato mediante la chiamata a startStepCounterService()
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
            }
        }
    }
}

