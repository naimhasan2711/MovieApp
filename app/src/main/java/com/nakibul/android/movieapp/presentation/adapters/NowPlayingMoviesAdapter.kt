package com.nakibul.android.movieapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nakibul.android.movieapp.R
import com.nakibul.android.movieapp.domain.models.NowPlayingMovie
import com.nakibul.android.movieapp.utils.Constants

class NowPlayingMoviesAdapter(
    val nowPlayingMovie: List<NowPlayingMovie>,
    val onItemClickListener: (NowPlayingMovie) -> Unit
) : RecyclerView.Adapter<NowPlayingMoviesAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.nowPlayingMoviePoster)

        init {
            itemView.setOnClickListener {
                val position = onItemClickListener.invoke(nowPlayingMovie[position])
            }
        }

        fun bind(nowPlayingMovie: NowPlayingMovie) {
            val requestOptions = RequestOptions().transform(
                CenterInside(),
                RoundedCorners(30)
            )
            val posterUrl = "${Constants.BASE_URL_IMAGE_PATH}${nowPlayingMovie.poster}"

            Glide
                .with(poster.context)
                .load(posterUrl)
                .apply(requestOptions)
                .placeholder(R.drawable.ic_film_reel2)
                .error(R.drawable.ic_film_reel2)
                .into(poster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_now_playing_movie, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nowPlayingMovie.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(nowPlayingMovie[position])
    }
}