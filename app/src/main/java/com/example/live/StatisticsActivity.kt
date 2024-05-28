package com.example.live
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val previous : Button = findViewById(R.id.back_sender_button)
        previous.setOnClickListener {
            finish()
        }
    }
}
