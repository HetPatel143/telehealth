package com.example.telehealth.data

data class Appointment(
    val doctorName: String = "",
    val specialty: String = "",
    val date: String = "",
    val time: String = "",
    val totalAmount: Double = 0.0,
)
