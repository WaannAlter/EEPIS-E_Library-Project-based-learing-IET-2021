package com.example.elibpl.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.elibpl.databinding.FragmentHomeBinding
import com.example.elibpl.databinding.FragmentGalleryBinding
import com.example.elibpl.model.EBook
import com.example.elibpl.ui.home.EBookAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseFirestore
    private lateinit var eBooks: List<EBook>
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firestore
        database = FirebaseFirestore.getInstance()

        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("e-booksfiksi")

        // Retrieve e-book data from Firebase Firestore
        val eBooksRef = database.collection("e-booksfiksi")
        eBooksRef.get()
            .addOnSuccessListener { querySnapshot ->
                displayEBooks(querySnapshot)
            }
            .addOnFailureListener { exception ->
                binding.textHome.text = "Failed to retrieve e-book data: ${exception.message}"
            }

        return root
    }

    private fun displayEBooks(querySnapshot: QuerySnapshot) {
        if (querySnapshot.isEmpty) {
            binding.textHome.text = "No e-books available."
            return
        }

        eBooks = querySnapshot.toObjects(EBook::class.java)

        val bookAdapter = EBookAdapter(eBooks) { fileName ->
            openPDF(fileName)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun openPDF(fileName: String) {
        if (fileName.isEmpty()) {
            // Handle the case when fileName is empty
            binding.textHome.text = "Invalid file name."
            return
        }

        val pdfRef = storageReference.child(fileName)
        pdfRef.downloadUrl
            .addOnSuccessListener { uri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                // Handle the failure to retrieve the PDF URL
                binding.textHome.text = "Failed to open PDF: ${exception.message}"
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
