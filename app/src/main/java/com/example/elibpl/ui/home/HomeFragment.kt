package com.example.elibpl.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.elibpl.databinding.FragmentHomeBinding
import com.example.elibpl.model.EBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var pdfView: PDFView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pdfView = binding.pdfView

        // Initialize Firebase Storage and Firestore
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()

        // Retrieve e-book data from Firebase Firestore
        val eBooksRef = database.collection("e-books")
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

        val eBooks = querySnapshot.documents
        val eBook = eBooks.firstOrNull()?.toObject(EBook::class.java)
        if (eBook != null) {
            val eBookFileRef = storage.getReferenceFromUrl(eBook.downloadUrl)
            val localFile = File.createTempFile("ebook", "pdf")
            eBookFileRef.getFile(localFile)
                .addOnSuccessListener {
                    pdfView.fromFile(localFile)
                        .defaultPage(0)
                        .enableAnnotationRendering(true)
                        .onLoad(object : OnLoadCompleteListener {
                            override fun loadComplete(numPages: Int) {
                                pdfView.zoomTo(0f)
                                pdfView.jumpTo(0)
                            }
                        })
                        .scrollHandle(DefaultScrollHandle(requireContext()))
                        .spacing(10) // set spacing between pages in dp
                        .load()
                }
                .addOnFailureListener { exception ->
                    binding.textHome.text = "Failed to load e-book content: ${exception.message}"
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
