package com.example.live

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.live.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.analytics.FirebaseAnalytics

class SignInActivity : AppCompatActivity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var fbAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fbAuth = FirebaseAuth.getInstance()
        fbAnalytics = FirebaseAnalytics.getInstance(this)
        if (checkLoggedInUser()) {
            return
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView3.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val userMail = binding.editTextText7.text.toString()
            val pw = binding.editTextTextPassword.text.toString()

            if (userMail.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "Non sono ammessi campi vuoti!", Toast.LENGTH_LONG).show()
            } else {
                fbAuth.signInWithEmailAndPassword(userMail, pw).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val sharedPref = getSharedPreferences("logged_users", Context.MODE_PRIVATE)
                        val edit = sharedPref.edit()
                        edit.putString("user", userMail)
                        edit.putString("pw", pw)
                        edit.apply()
                        // Manda dati utente a Firebase
                        val userData = collectUserData()
                        fbAnalytics.logEvent("login", userData)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("user_email", userMail)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login fallito, riprovare.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInUser(): Boolean {
        val sharedPref = getSharedPreferences("logged_users", Context.MODE_PRIVATE)
        val loggedUser = sharedPref.getString("user", null)
        val loggedPw = sharedPref.getString("pw", null)

        if (loggedUser != null && loggedPw != null) {
            fbAuth.signInWithEmailAndPassword(loggedUser, loggedPw).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Invia dati utente a Firebase
                    val userData = collectUserData()
                    fbAnalytics.logEvent("login", userData)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("user_email", loggedUser)
                    startActivity(intent)
                    finish()
                } else {
                    val edit = sharedPref.edit()
                    edit.remove("user")
                    edit.remove("pw")
                    edit.apply()
                }
            }
            return true
        }
        return false
    }

    private fun collectUserData() : Bundle {
        val dataBundle = Bundle()
        // Info dispositivo
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