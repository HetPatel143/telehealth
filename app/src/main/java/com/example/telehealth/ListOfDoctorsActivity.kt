package com.example.telehealth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.adopter.DoctorAdapter
import com.example.telehealth.data.Doctor
import com.google.firebase.firestore.FirebaseFirestore

class ListOfDoctorsActivity : AppCompatActivity() {

    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_doctors)

        val selectedCategory = intent.getStringExtra("CATEGORY") // Get the selected category

        recyclerView = findViewById(R.id.recyclerViewDoctors)
        recyclerView.layoutManager = LinearLayoutManager(this)

        firestore = FirebaseFirestore.getInstance()

        // Fetch doctors for the selected category
        if (selectedCategory != null) {
            fetchDoctorsForCategory(selectedCategory)
        }
    }

    private fun fetchDoctorsForCategory(category: String) {
        firestore.collection("doctor")
            .document(category)
            .collection("Doctors")
            .get()
            .addOnSuccessListener { result ->
                val doctorList = mutableListOf<Doctor>()
                for (document in result.documents) {
                    val doctor = document.toObject(Doctor::class.java)
                    if (doctor != null) {
                        doctorList.add(doctor)
                    }
                }
                updateDoctorAdapter(doctorList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load doctors: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDoctorAdapter(doctorList: List<Doctor>) {
        doctorAdapter = DoctorAdapter(doctorList, this) { doctor ->
            // Handle booking click (optional)
            Toast.makeText(this, "Booked: ${doctor.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = doctorAdapter
    }
}
