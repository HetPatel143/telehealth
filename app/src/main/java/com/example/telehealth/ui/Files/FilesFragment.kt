package com.example.telehealth.ui.Files

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import com.example.telehealth.adopter.FileAdapter
import com.example.telehealth.data.FileModel
import com.example.telehealth.databinding.FragmentFilesBinding

class FilesFragment : Fragment() {

    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val PDF_REQUEST_CODE = 1
    private lateinit var fileAdapter: FileAdapter // Adapter for RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Storage and Firestore
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        val recyclerView = binding.recyclerViewFiles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fileAdapter = FileAdapter { fileUrl -> openPdf(fileUrl) }
        recyclerView.adapter = fileAdapter

        // Fetch and display files from Firestore
        fetchFilesFromFirestore()

        return root
    }

    private fun openPdf(fileUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No application available to view PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFilesFromFirestore() {
        firestore.collection("uploadedFiles")
            .get()
            .addOnSuccessListener { documents ->
                val filesList = mutableListOf<FileModel>()
                for (document in documents) {
                    val file = document.toObject(FileModel::class.java)
                    filesList.add(file)
                }
                fileAdapter.submitList(filesList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load files", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
