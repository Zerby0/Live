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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.logrocket.core.SDK
class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var stepCount: Int = 0
    private var calorieCount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup di Firebase
        setupFirebasePersistence()

        // Inizia il servizio per il conteggio dei passi
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Setup della toolbar
        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setLogo(R.drawable.logo)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        //LogRocket
        userData["email"] = intent.getStringExtra("user_email").orEmpty()
        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

        // Inizializza il Room database
        LiveDatabase.getDatabase(this)

        // Setup del BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Check dei permessi in real-time
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

        // Inizializza le SharedPreferences
        sharedPreferences = getSharedPreferences("stepCounterPrefs", Context.MODE_PRIVATE)

        val userEmail = intent.getStringExtra("user_email")
        userEmail?.let {
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("user_email", it)
                apply()
            }
        }
    }
    // Funzione per inizializzare la persistenza di Firebase
    private fun setupFirebasePersistence() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: DatabaseException) {

            Log.w(ContentValues.TAG, "Firebase persistence is already enabled")
        }
    }
    // Funzione per ottenere le SharedPreferences dell'utente
    private fun getUserSpecificPreferences(userEmail: String): SharedPreferences {
        return getSharedPreferences("stepCounterPrefs_$userEmail", Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveUserData()
    }
    // Funzione per salvare i dati dell'utente
    private fun saveUserData() {
        val userEmail = intent.getStringExtra("user_email") ?: return
        val userPrefs = getUserSpecificPreferences(userEmail)
        val editor = userPrefs.edit()
        editor.putInt("stepCount", stepCount)
        editor.putFloat("calorieCount", calorieCount.toFloat())
        editor.apply()
    }
    // Funzione per avviare il servizio di conteggio dei passi
    private fun startStepCounterService() {
        val intent = Intent(this, StepCounterService::class.java)
        startForegroundService(intent)
    }
    // Funzione per gestire la scelta dell'utente sui permessi
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
    // Funzione per gestire la scelta dell'utente nella toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
