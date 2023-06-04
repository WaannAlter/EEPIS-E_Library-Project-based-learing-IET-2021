package com.example.elibpl.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elibpl.model.EBook

class EBookAdapter(
    private val eBooks: List<EBook>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<EBookAdapter.ViewHolder>() {

    class ViewHolder(view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eBook = eBooks[position]
        val textView = holder.itemView as TextView
        textView.text = eBook.title
        holder.itemView.setOnClickListener {
            onItemClick(eBook.fileName)
        }
    }

    override fun getItemCount(): Int = eBooks.size
}
