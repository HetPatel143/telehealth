package com.example.telehealth.adopter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.DoctorDetails
import com.example.telehealth.R
import com.example.telehealth.data.Doctor

class DoctorAdapter(private val doctorList: List<Doctor>, private val context: Context, private val onBookClick: (Doctor) -> Unit) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorImage: ImageView = itemView.findViewById(R.id.doctor_image)
        val doctorName: TextView = itemView.findViewById(R.id.doctor_name)
        val doctorDescription: TextView = itemView.findViewById(R.id.doctor_description)
        val doctorRating: TextView = itemView.findViewById(R.id.doctor_rating)
        val bookButton: Button = itemView.findViewById(R.id.book_button)

        fun bind(doctor: Doctor) {
            doctorName.text = doctor.name
            doctorDescription.text = doctor.about

            // Start DoctorDetails activity when book button is clicked
            bookButton.setOnClickListener {
                val intent = Intent(context, DoctorDetails::class.java)
                // Pass the doctor's name to the next activity
                intent.putExtra("doctorName", doctor.name)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rvdoctors, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctorList[position])
    }

    override fun getItemCount(): Int = doctorList.size
}
