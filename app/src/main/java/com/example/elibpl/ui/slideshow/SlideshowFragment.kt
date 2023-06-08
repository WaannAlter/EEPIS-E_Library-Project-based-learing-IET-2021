package com.example.elibpl.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elibpl.data.LoginDataSource
import com.example.elibpl.data.LoginRepository
import com.example.elibpl.databinding.FragmentSlideshowBinding
import com.example.elibpl.model.EBook
import com.example.elibpl.ui.home.EBookAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Calendar

class SlideshowFragment(private val loginRepository: LoginRepository) : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseFirestore
    private lateinit var eBooks: List<EBook>
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firestore
        database = FirebaseFirestore.getInstance()

        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("e-modul")

        // Retrieve e-book data from Firebase Firestore
        val eBooksRef = database.collection("e-modul")
        eBooksRef.get()
            .addOnSuccessListener { querySnapshot ->
                displayEBooks(querySnapshot)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to retrieve e-book data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        return root
    }

    private fun displayEBooks(querySnapshot: QuerySnapshot) {
        if (querySnapshot.isEmpty) {
            Toast.makeText(requireContext(), "No e-books available.", Toast.LENGTH_SHORT).show()
            return
        }

        eBooks = querySnapshot.toObjects(EBook::class.java)

        val bookAdapter = EBookAdapter(eBooks, { ebook -> borrowBook(ebook.id, "<Borrower ID>") }, { ebook -> openPDF(ebook.fileName) })


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun openPDF(fileName: String) {
        if (fileName.isEmpty()) {
            // Handle the case when fileName is empty
            Toast.makeText(requireContext(), "Invalid file name.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Failed to open PDF: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun borrowBook(bookId: String, borrowerId: String) {
        val yourDataSource = LoginDataSource(context)
        val loginRepository = LoginRepository(yourDataSource) // YourDataSource should be replaced with your actual data source
        val currentUser = loginRepository.user
        val bookAdapter = EBookAdapter(
            eBooks,
            { ebook -> currentUser?.let { borrowBook(ebook.fileName, it.userId) } },
            { ebook -> openPDF(ebook.fileName) }
        )
        val borrowedStartDate = Calendar.getInstance().time

        // Add 14 days to borrowedStartDate to get borrowedEndDate
        val calendar = Calendar.getInstance()
        calendar.time = borrowedStartDate
        calendar.add(Calendar.DAY_OF_YEAR, 14)
        val borrowedEndDate = calendar.time

        // Update book in the database with the new borrowed status
        val bookRef = database.collection("books    ").document(bookId)
        bookRef.update(
            "isBorrowed", true,
            "borrowedBy", borrowerId,
            "borrowedStartDate", borrowedStartDate,
            "borrowedEndDate", borrowedEndDate
        )
        // Handle database update success and failure cases
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
