package com.example.live

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.live.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fbAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fbAuth = FirebaseAuth.getInstance()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "La password deve contenere almeno 6 caratteri.", Toast.LENGTH_SHORT)
            .show()

        binding.textView2.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val userMail = binding.editTextText4.text.toString()
            val pw = binding.editTextText5.text.toString()
            val pwConfirm = binding.editTextText6.text.toString()

            if (userMail.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty()) {
                Toast.makeText(this, "Non sono ammessi campi vuoti!", Toast.LENGTH_LONG).show()
            } else {
                if (pw != pwConfirm) {
                    Toast.makeText(
                        this,
                        "Le due password inserite non corrispondono!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    fbAuth.createUserWithEmailAndPassword(userMail, pw)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
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