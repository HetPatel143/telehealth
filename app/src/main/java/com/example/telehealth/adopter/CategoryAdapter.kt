package com.example.telehealth.adopter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit // A lambda to handle click events
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.doctorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rvcatagories, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.doctorName.text = categories[position]
        holder.itemView.setOnClickListener {
            onCategoryClick(categories[position]) // Trigger the click event
        }
    }

    override fun getItemCount(): Int = categories.size
}

