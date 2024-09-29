package com.example.telehealth

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DoctorDetails : AppCompatActivity() {

    private lateinit var btnDatePicker: Button
    private lateinit var btnTimePicker: Button
    private lateinit var btnBookAppointment: Button
    private lateinit var calendar: Calendar
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private var appointmentDuration: Int = 1 // Default to 1 hour
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        val doctorName: TextView = findViewById(R.id.tvDoctorName)
        val doctorSpecialty: TextView = findViewById(R.id.tvDoctorSpecialty)
        val doctorPayment: TextView = findViewById(R.id.tvPaymentInfo)
        val doctorDescription: TextView = findViewById(R.id.tvAppointmentDetails)
        btnDatePicker = findViewById(R.id.btnDatePicker)
        btnTimePicker = findViewById(R.id.btnTimePicker)
        btnBookAppointment = findViewById(R.id.btnBookAppointment)
        calendar = Calendar.getInstance()

        firestore = FirebaseFirestore.getInstance()

        val doctorNameFromIntent = intent.getStringExtra("doctorName")

        if (doctorNameFromIntent != null) {
            fetchDoctorDetails(doctorNameFromIntent, doctorName, doctorSpecialty, doctorPayment, doctorDescription)
        }

        btnDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        btnTimePicker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    Toast.makeText(this, "Selected Time: $selectedTime", Toast.LENGTH_SHORT).show()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        btnBookAppointment.setOnClickListener {
            // Pass details to PaymentActivity
            val paymentIntent = Intent(this, PaymentActivity::class.java)
            paymentIntent.putExtra("doctorName", doctorName.text.toString())
            paymentIntent.putExtra("specialty", doctorSpecialty.text.toString())
            paymentIntent.putExtra("pricePerHour", 200) // Price per hour
            paymentIntent.putExtra("duration", appointmentDuration) // Duration in hours
            paymentIntent.putExtra("selectedDate", selectedDate)
            paymentIntent.putExtra("selectedTime", selectedTime)
            startActivity(paymentIntent)
        }
    }

    private fun fetchDoctorDetails(
        doctorName: String,
        doctorNameTextView: TextView,
        doctorSpecialtyTextView: TextView,
        doctorPaymentTextView: TextView,
        doctorDescriptionTextView: TextView
    ) {
        firestore.collection("doctor")
            .get()
            .addOnSuccessListener { doctorCollection ->
                for (doctorDocument in doctorCollection.documents) {
                    doctorDocument.reference.collection("Doctors")
                        .whereEqualTo("name", doctorName)
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                val doctorData = result.documents[0]
                                doctorNameTextView.text = doctorData.getString("name")
                                doctorSpecialtyTextView.text = doctorData.getString("Denteeth")
                                doctorPaymentTextView.text = "Payment: $${doctorData.getString("Price")}"
                                doctorDescriptionTextView.text = doctorData.getString("about")
                            } else {
                            }
                        }
                        .addOnFailureListener { e ->

                        }
                }
            }
            .addOnFailureListener { e ->

            }
    }
}
