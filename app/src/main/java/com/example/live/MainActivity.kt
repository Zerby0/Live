package com.example.live

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.logrocket.core.SDK

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences
    var stepCount: Int = 0
    var calorieCount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //gestione toolbar
        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Rimuove il titolo dell'app dalla Toolbar
        supportActionBar?.setLogo(R.drawable.logo)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        // Per LogRocket
        userData["email"] = intent.getStringExtra("user_email").orEmpty()
        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

        // Inizializza Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        // Inizializza il database Room
        LiveDatabase.getDatabase(this)


        // Per navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Verifica i permessi a run time in base alla versione Android (dalla <=10 non serve, quindi non controlla)
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

        // Inizializza SharedPreferences
        sharedPreferences = getSharedPreferences("stepCounterPrefs", Context.MODE_PRIVATE)

        // Carica il conteggio dei passi salvato e aggiorna la TextView
        val savedStepCount = sharedPreferences.getInt("stepCount", 0)
        val savedCalorieCount = sharedPreferences.getFloat("calorieCount", 0.0f).toDouble()
        stepCount = savedStepCount
        calorieCount = savedCalorieCount
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startStepCounterService() {
        val intent = Intent(this, StepCounterService::class.java)
        startForegroundService(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Handle logout logic here
                val sharedPrefLogin = getSharedPreferences("logged_users", Context.MODE_PRIVATE)
                val edit = sharedPrefLogin.edit()
                edit.remove("user")
                edit.remove("pw")
                edit.apply()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_settings -> {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.ProfileFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
