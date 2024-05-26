package com.example.live
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
class BiometricsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.biometrics_data)

        val previous : Button = findViewById(R.id.back_sender_button)
        previous.setOnClickListener {
            finish()
        }

    }
}
