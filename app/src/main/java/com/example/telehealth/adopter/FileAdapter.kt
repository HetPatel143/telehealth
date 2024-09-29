package com.example.telehealth.adopter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.data.FileModel
import com.example.telehealth.databinding.ItemFileBinding

class FileAdapter(private val onFileClick: (String) -> Unit) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private var filesList: List<FileModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = filesList[position]
        holder.bind(file)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    fun submitList(files: List<FileModel>) {
        filesList = files
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: FileModel) {
            binding.fileName.text = file.fileName

            // Set click listener to open PDF
            binding.root.setOnClickListener {
                onFileClick(file.fileUrl)
            }
        }
    }
}
