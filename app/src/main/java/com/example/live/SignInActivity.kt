package com.example.live

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.live.databinding.ActivitySignInBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var fbAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fbAuth = FirebaseAuth.getInstance()
        fbAnalytics = FirebaseAnalytics.getInstance(this)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user had already logged in, skip everything and switch to MainActivity
        val sharedPref = getSharedPreferences("logged_users", Context.MODE_PRIVATE)
        val loggedUser = sharedPref.getString("user", null)
        val loggedPw = sharedPref.getString("pw", null)

        if (loggedUser != null && loggedPw != null) {
            fbAuth.signInWithEmailAndPassword(loggedUser, loggedPw).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Send user data to Firebase
                    val userData = collectUserData()
                    fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, userData)
                    // Send user to Main Activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login automatico fallito.", Toast.LENGTH_LONG).show()
                }
            }

        }

        // Send user to sign-up if not registered
        binding.textView3.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Wait for user sign-in action
        binding.button2.setOnClickListener {
            // Get user data
            val userMail = binding.editTextText7.text.toString()
            val pw = binding.editTextTextPassword.text.toString()

            // Check for empty fields
            if (userMail.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "Non sono ammessi campi vuoti!", Toast.LENGTH_LONG).show()
            } else {
                fbAuth.signInWithEmailAndPassword(userMail, pw).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        // Save login data in SharedPreferences for future auto-sign-in
                        val edit = sharedPref.edit()
                        edit.putString("user", userMail)
                        edit.putString("pw", pw)
                        // Close editor
                        edit.apply()
                        // Send user data to Firebase
                        val userData = collectUserData()
                        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, userData)
                        // Move user to Main Activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this, "Login fallito, riprovare.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun collectUserData() : Bundle {
        val dataBundle = Bundle()
        // Device info
        dataBundle.apply {
            putLong("time", System.currentTimeMillis())
            putString("device", android.os.Build.DEVICE)
            putString("model", android.os.Build.MODEL)
            putString("brand", android.os.Build.BRAND)
            putString("manufacturer", android.os.Build.MANUFACTURER)
            putString("hardware", android.os.Build.HARDWARE)
            putString("deviceOS", android.os.Build.VERSION.RELEASE)
        }

        return dataBundle
    }



}