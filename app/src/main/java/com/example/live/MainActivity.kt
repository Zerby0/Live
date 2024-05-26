package com.example.live

import  android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.logrocket.core.SDK
import android.content.Intent
import android.widget.Button
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        userData["name"] = "Sim"
        userData["email"] = "trial@gmail.com"
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
    }
}

