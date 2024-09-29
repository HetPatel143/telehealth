package com.example.telehealth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.telehealth.doctordetails.CongratulationActivity
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {

    private lateinit var btnPayNow: Button
    private lateinit var txtTotalAmount: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        btnPayNow = findViewById(R.id.btnPayNow)
        txtTotalAmount = findViewById(R.id.txtTotalAmount)

        // Get data from the previous activity
        val doctorName = intent.getStringExtra("doctorName")
        val specialty = intent.getStringExtra("specialty")
        val pricePerHour = intent.getIntExtra("pricePerHour", 200) // Default price
        val duration = intent.getIntExtra("duration", 1) // Default duration
        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedTime = intent.getStringExtra("selectedTime")

        // Calculate total amount
        val totalAmount = pricePerHour * duration
        txtTotalAmount.text = "$$totalAmount" // Set total amount to the TextView

        // Pay Now button click listener
        btnPayNow.setOnClickListener {
            // Add the appointment to Firestore
            addAppointmentToFirestore(doctorName, specialty, totalAmount, duration, selectedDate, selectedTime)
        }
    }

    private fun addAppointmentToFirestore(
        doctorName: String?,
        specialty: String?,
        totalAmount: Int,
        duration: Int,
        selectedDate: String?,
        selectedTime: String?
    ) {

        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("USER_NAME", null)

        // Assuming a user ID is available (replace with actual user ID retrieval)
        val userId = email // Replace with actual user ID

        val appointmentData = hashMapOf(
            "doctorName" to doctorName,
            "specialty" to specialty,
            "totalAmount" to totalAmount,
            "duration" to duration,
            "date" to selectedDate,
            "time" to selectedTime,
            "userId" to userId
        )

        firestore.collection("appointments")
            .add(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to the congratulation screen
                val intent = Intent(this, CongratulationActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error booking appointment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
