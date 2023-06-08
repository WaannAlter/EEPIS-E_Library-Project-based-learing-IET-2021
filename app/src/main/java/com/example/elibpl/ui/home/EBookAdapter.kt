package com.example.elibpl.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elibpl.databinding.ItemEbookBinding
import com.example.elibpl.model.EBook
import com.bumptech.glide.Glide

class EBookAdapter(
    private val eBooks: List<EBook>,
    private val onBorrowClick: (EBook) -> Unit,
    private val onReadClick: (EBook) -> Unit
) : RecyclerView.Adapter<EBookAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEbookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEbookBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eBook = eBooks[position]
        with(holder.binding) {
            textViewTitle.text = eBook.title
            textViewAuthor.text = eBook.author
            buttonPinjam.setOnClickListener {
                onBorrowClick(eBook)
            }
            buttonBaca.setOnClickListener {
                onReadClick(eBook)
            }
            Glide.with(imageViewPreview.context)
                .load("@drawable/fkc9ulbxmaudjb_.jpg")
                .into(imageViewPreview)
        }
    }

    override fun getItemCount(): Int = eBooks.size
}
