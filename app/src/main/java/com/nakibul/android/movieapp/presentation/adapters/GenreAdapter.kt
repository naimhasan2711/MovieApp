package com.nakibul.android.movieapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nakibul.android.movieapp.R
import com.nakibul.android.movieapp.domain.models.Genre

class GenreAdapter(private val genres: List<Genre>) :
    RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val genreName: TextView = itemView.findViewById(R.id.genresItemName)

        fun bind(item: Genre) {
            genreName.text = item.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return genres.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(genres[position])
    }
}