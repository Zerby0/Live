package com.example.live

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.logrocket.core.SDK


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(ContentValues.TAG, "Si sta avviando l'app")
        val userData: MutableMap<String, String> = HashMap()

        userData["name"] = "Zerby"
        userData["email"] = "sonoGay@gmail.com"
        userData["subscriptionPlan"] = "premium"

        SDK.identify("28dvm2jfa", userData)
        Log.v(ContentValues.TAG, "Identity funziona")

    }
}

