package com.example.telehealth.doctordetails

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.telehealth.MainActivity
import com.example.telehealth.R

class CongratulationActivity : AppCompatActivity() {

    private lateinit var btnDone: Button
    private lateinit var txtMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulation)

        btnDone = findViewById(R.id.btnDone)
        txtMessage = findViewById(R.id.txtCongratulatoryMessage)

        txtMessage.text = "Congratulations! Your appointment has been booked."

        btnDone.setOnClickListener {
            // Navigate back to the main screen or any desired activity
            val intent = Intent(this, MainActivity::class.java) // Replace with your main activity
            startActivity(intent)
            finish()
        }
    }
}
