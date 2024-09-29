package com.example.telehealth.ui.Appointments

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.AppConstants
import com.example.telehealth.Calling
import com.example.telehealth.adopter.AppointmentAdapter
import com.example.telehealth.data.Appointment
import com.example.telehealth.databinding.FragmentAppointmentsBinding

import com.google.firebase.firestore.FirebaseFirestore
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var appointmentAdapter: AppointmentAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Zego Call Service

        firestore = FirebaseFirestore.getInstance()

        // Fetch user email from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("USER_NAME", null)

        if (email != null) {
            // Fetch appointments based on user email
            retrieveAppointmentsForUser(email)
        } else {
            Toast.makeText(requireContext(), "No email found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    // Initialize Zego Call Service

    private fun retrieveAppointmentsForUser(email: String) {
        firestore.collection("appointments")
            .whereEqualTo("userId", email)
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
                        appointments.add(Appointment(doctorName, specialty, date, time, totalAmount))
                    }

                    // Set up the AppointmentAdapter with the list of appointments
                    appointmentAdapter = AppointmentAdapter(requireContext(), appointments)
                    binding.recyclerViewAppointments.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.recyclerViewAppointments.adapter = appointmentAdapter

                } else {
                    Toast.makeText(requireContext(), "No appointments found for this user", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateAndTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}