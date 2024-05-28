package com.example.live

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.live.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fbAuth = FirebaseAuth.getInstance()

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

}