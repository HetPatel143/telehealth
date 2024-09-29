package com.example.telehealth.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.ListOfDoctorsActivity
import com.example.telehealth.adopter.AppointmentAdapter
import com.example.telehealth.adopter.CategoryAdapter
import com.example.telehealth.adopter.DoctorAdapter
import com.example.telehealth.data.Appointment
import com.example.telehealth.data.Doctor
import com.example.telehealth.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    // Using view binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        // Fetch user email from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("USER_NAME", null)

        if (email != null) {
            // Fetch user data based on the email document ID from the "Users" collection
            firestore.collection("Users")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve the username from the document
                        val userName = document.getString("username")
                        if (userName != null) {
                            binding.UserNameTV.text = userName
                        } else {
                            Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "User document not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Fetch appointments based on user email
            retrieveAppointmentsForUser(email)
        } else {
            Toast.makeText(requireContext(), "No email found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }

        // Set up category RecyclerView
        val categories = listOf(
            "Dentheeth", "Physiotherapist", "therapist"
        )

        // Set up category RecyclerView with category click handling
        categoryAdapter = CategoryAdapter(categories) { selectedCategory ->
            val intent = Intent(requireContext(), ListOfDoctorsActivity::class.java)
            intent.putExtra("CATEGORY", selectedCategory) // Pass the selected category to the new activity
            startActivity(intent)
        }

        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter

        // Set up doctor RecyclerView
        binding.recyclerViewDoctors.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        retrieveAllDoctors()
    }

    private fun retrieveAppointmentsForUser(email: String) {
        // Query Firestore to get all appointments for the user
        firestore.collection("appointments")
            .whereEqualTo("userId", email) // Query for appointments where userId equals the user's email
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val appointments = mutableListOf<Appointment>()

                    for (document in querySnapshot.documents) {
                        // Extract appointment details from each document
                        val doctorName = document.getString("doctorName") ?: ""
                        val date = document.getString("date") ?: ""
                        val time = document.getString("time") ?: ""
                        val totalAmount = document.getDouble("totalAmount") ?: 0.0
                        val specialty = document.getString("specialty") ?: ""

                        // Add each appointment to the list
                        appointments.add(
                            Appointment(doctorName, specialty, date, time, totalAmount)
                        )
                    }

                    // Set up the AppointmentAdapter with the list of appointments
                    appointmentAdapter = AppointmentAdapter(requireContext(),appointments)
                    binding.recyclerViewAppointments.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerViewAppointments.adapter = appointmentAdapter
                } else {
                    Toast.makeText(requireContext(), "No appointments found for this user", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun retrieveAllDoctors() {
        firestore.collection("doctor")
            .get()
            .addOnSuccessListener { doctorTypeDocuments ->
                val allDoctorsList = mutableListOf<Doctor>()

                for (doctorTypeDocument in doctorTypeDocuments) {
                    val doctorsTask = firestore.collection("doctor")
                        .document(doctorTypeDocument.id)
                        .collection("Doctors")
                        .get()

                    doctorsTask.addOnSuccessListener { result ->
                        for (doctorDocument in result.documents) {
                            val doctor = doctorDocument.toObject(Doctor::class.java)
                            if (doctor != null) {
                                allDoctorsList.add(doctor)
                            }
                        }

                        updateDoctorAdapter(allDoctorsList)
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error loading doctors for ${doctorTypeDocument.id}: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading doctor types: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDoctorAdapter(doctorList: List<Doctor>) {
        doctorAdapter = DoctorAdapter(doctorList, requireContext()) { doctor ->
            // Handle booking click (e.g., Toast message for booked doctor)
            Toast.makeText(requireContext(), "Booked: ${doctor.name}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewDoctors.adapter = doctorAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
