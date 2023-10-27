package com.nakibul.android.movieapp.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nakibul.android.movieapp.R
import com.nakibul.android.movieapp.domain.models.TrendingMovie
import com.nakibul.android.movieapp.utils.Constants

class TrendingMoviesAdapter(
    var movies: List<TrendingMovie>,
    val onItemClickListener: (TrendingMovie) -> Unit,
    val onSaveButtonClickListener: (TrendingMovie) -> Unit
) : RecyclerView.Adapter<TrendingMoviesAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.trendingMovieLinearListItemTitle)
        private var rate: TextView = itemView.findViewById(R.id.trendingMovieLinearListItemRate)
        private var poster: ImageView =
            itemView.findViewById(R.id.trendingMovieLinearListItemPoster)
        private var saveBtn: ImageView = itemView.findViewById(R.id.trendingMovieListSaveBtn)

        init {
            itemView.setOnClickListener {
                val position = onItemClickListener.invoke(movies[position])
            }

            saveBtn.setOnClickListener {
                val position = onSaveButtonClickListener.invoke(movies[position])
            }

        }

        fun bind(item: TrendingMovie) {
            title.text = item.title
            rate.text = item.voteAverage.toString()

            var url = ""
            var requestOptions = RequestOptions()

            if (item.isSaved) {
                saveBtn.setImageResource(R.drawable.ic_saved)
            } else {
                saveBtn.setImageResource(R.drawable.ic_unsaved)
            }

            when (currentLayoutMode) {
                1 -> {
                    url = "${Constants.BASE_URL_IMAGE_PATH}${item.posterPath}"
                    requestOptions = RequestOptions().transform(CircleCrop(), RoundedCorners(50))
                }

                2 -> {
                    url = "${Constants.BASE_URL_IMAGE_PATH}${item.backdropPath}"
                    requestOptions = RequestOptions().transform(CircleCrop())
                }
            }

            Glide
                .with(poster.context)
                .load(url)
                .apply(requestOptions)
                .placeholder(R.drawable.ic_film_reel)
                .error(R.drawable.ic_film_reel)
                .into(poster)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_GRID) {
            Log.i("Adapter", "VIEW_TYPE_LINEAR")

            val linearView =
                layoutInflater.inflate(R.layout.item_linear_trending_movie, parent, false)
            ViewHolder(linearView)
        } else {
            Log.i("Adapter", "VIEW_TYPE_GRID")

            val gridView = layoutInflater.inflate(R.layout.item_grid_trending_movie, parent, false)
            ViewHolder(gridView)
        }
    }

    override fun onBindViewHolder(holder: TrendingMoviesAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    private companion object {
        const val VIEW_TYPE_GRID = 1
        const val VIEW_TYPE_LINEAR = 2
    }

    private var currentLayoutMode = VIEW_TYPE_GRID
    override fun getItemViewType(position: Int): Int {
        return currentLayoutMode
    }

    fun updateMovieSavedStatus(movieId: Int, isSaved: Boolean) {
        val position = movies.indexOfFirst {
            it.id == movieId
        }
        if (position != -1) {
            movies[position].isSaved = isSaved
            notifyItemChanged(position)
        }
    }
}