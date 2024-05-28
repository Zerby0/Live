package com.example.live

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.live.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    // Initiate firebase authentication instance
    private lateinit var fbAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fbAuth = FirebaseAuth.getInstance()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "La password deve contenere almeno 6 caratteri.", Toast.LENGTH_SHORT).show()

        // Wait for user "already registered" action
        binding.textView2.setOnClickListener {
            // If user is already registered, send him to sign-in
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Wait for user sign-up action
       binding.button.setOnClickListener {
            // Get user data
            val userMail = binding.editTextText4.text.toString()
            val pw = binding.editTextText5.text.toString()
            val pwConfirm =  binding.editTextText6.text.toString()

            // Check for empty fields
            if (userMail.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty()) {
                Toast.makeText(this, "Non sono ammessi campi vuoti!", Toast.LENGTH_LONG).show()
            } else {
                // Check for matching password and password confirmation
                if (pw != pwConfirm) {
                    Toast.makeText(
                        this,
                        "Le due password inserite non corrispondono!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Sign-up implementation
                    fbAuth.createUserWithEmailAndPassword(userMail, pw).addOnCompleteListener(this) {
                        if (it.isSuccessful) {  // Firebase setup successful, send user to sign-in page
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {    // Firebase setup failed, send pop-up
                            Toast.makeText(
                            this,
                            "Autenticazione fallita, riprovare.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            }
        }
    }
}