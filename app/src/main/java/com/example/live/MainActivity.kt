package com.example.live

import  android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.logrocket.core.SDK
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        userData["name"] = "Zerby"
        userData["email"] = "trial@gmail.com"
        userData["subscriptionPlan"] = "premium"

        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

    }
}

