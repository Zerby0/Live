package com.example.live

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.logrocket.core.SDK
import android.content.Intent
import android.Manifest
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), StepCountListener {

    private val REQUEST_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences
    var stepCount: Int = 0
    var calorieCount: Double = 0.0

    //Broadcast receiver per i passi contati
    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stepCount = intent?.getIntExtra("stepCount", 0) ?: 0
            calorieCount = intent?.getDoubleExtra("calorieCount", 0.0) ?: 0.0
            Log.d("MainActivity", "Received step count: $stepCount")

            //Invia i dati al fragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            if (fragment is HomeFragment) {
                fragment.updateStepCount(stepCount, calorieCount)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        //Per LogRocket
        userData["name"] = "Andrea"
        userData["email"] = "AndreaPadovan@gmail.com"
        userData["subscriptionPlan"] = "premium"
        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

        //Per navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        //Verifica i permessi a run time in base alla versione Android (dalla <=10 non serve, quindi non controlla)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_CODE)
            } else {
                startStepCounterService()
            }
        } else {
            startStepCounterService()
        }

        // TODO: da rimuovere quando avremo il database
        // Inizializza SharedPreferences
        sharedPreferences = getSharedPreferences("stepCounterPrefs", Context.MODE_PRIVATE)

        // Carica il conteggio dei passi salvato e aggiorna la TextView
        val savedStepCount = sharedPreferences.getInt("stepCount", 0)
        val savedCalorieCount = sharedPreferences.getFloat("calorieCount", 0.0f).toDouble()
        stepCount = savedStepCount
        calorieCount = savedCalorieCount

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
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
            }
        }
    }
    override fun onStepCountChanged(stepCount: Int, calorieCount: Double) {
        // Invia i dati al fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (fragment is HomeFragment) {
            fragment.updateStepCount(stepCount, calorieCount)
        }
    }
}

