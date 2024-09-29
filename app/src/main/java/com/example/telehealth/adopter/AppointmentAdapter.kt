package com.example.telehealth.adopter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.data.Appointment
import com.example.telehealth.Calling

class AppointmentAdapter(
    private val context: Context,
    private val appointments: List<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.doctor_name)
        val doctorSpecialty: TextView = view.findViewById(R.id.doctor_specialty)
        val appointmentDate: TextView = view.findViewById(R.id.appointment_date)
        val appointmentTime: TextView = view.findViewById(R.id.appointment_time)
        val doctorImage: ImageView = view.findViewById(R.id.doctor_image)

        // Bind function to bind the data and set the click listener
        fun bind(appointment: Appointment) {
            doctorName.text = appointment.doctorName
            doctorSpecialty.text = appointment.specialty
            appointmentDate.text = appointment.date
            appointmentTime.text = appointment.time

            // Set onClickListener to handle item click and navigate to Calling activity
            itemView.setOnClickListener {
                val intent = Intent(context, Calling::class.java)
                intent.putExtra("Doctor_NAME", appointment.doctorName)
                // Add any other data you want to pass
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rvappoinmnets, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        // Use the bind function to set the data and handle the click event
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int = appointments.size
}