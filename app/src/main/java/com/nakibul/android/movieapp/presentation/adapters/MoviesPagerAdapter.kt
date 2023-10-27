package com.nakibul.android.movieapp.presentation.adapters

import android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.nakibul.android.movieapp.R
import com.nakibul.android.movieapp.domain.models.TrendingMovie
import com.nakibul.android.movieapp.utils.Constants
import javax.inject.Inject

class MoviesPagerAdapter @Inject constructor(
    val onItemClickListener: ((TrendingMovie) -> Unit),
    val onSaveButtonClickListener: ((TrendingMovie) -> Unit)
) : PagingDataAdapter<TrendingMovie, MoviesPagerAdapter.ViewHolder>(MovieComparator) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var title: TextView = itemView.findViewById(R.id.trendingMovieLinearListItemTitle)
        private var rate: TextView = itemView.findViewById(R.id.trendingMovieLinearListItemRate)
        private var poster: ImageView =
            itemView.findViewById(R.id.trendingMovieLinearListItemPoster)
        private var saveBtn: ImageView = itemView.findViewById(R.id.trendingMovieListSaveBtn)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                onItemClickListener.invoke(getItem(position)!!)
            }
            saveBtn.setOnClickListener {
                onSaveButtonClickListener.invoke(getItem(position)!!)
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
                    url = "${Constants.BASE_URL_IMAGE_PATH}${item.posterPath}"
                    requestOptions = RequestOptions().transform(CenterCrop())
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

    override fun onBindViewHolder(holder: MoviesPagerAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoviesPagerAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GRID) {
            val linearView =
                layoutInflater.inflate(R.layout.item_linear_trending_movie, parent, false)
            ViewHolder(linearView)
        } else {
            val gridView = layoutInflater.inflate(R.layout.item_grid_trending_movie, parent, false)
            ViewHolder(gridView)
        }
    }

    fun updateMovieSavedStatus(movieId: Int, isSaved: Boolean) {
        val index = snapshot().indexOfFirst { it?.id == movieId }
        if (index != -1) {
            val currentList = snapshot().toMutableList()
            currentList[index]?.isSaved = isSaved
            notifyItemChanged(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun switchToGridMode() {
        if (currentLayoutMode != VIEW_TYPE_GRID) {
            currentLayoutMode = VIEW_TYPE_GRID
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun switchToLinearMode() {
        if (currentLayoutMode != VIEW_TYPE_LINEAR) {
            currentLayoutMode = VIEW_TYPE_LINEAR
            notifyDataSetChanged()
        }
    }

    object MovieComparator : DiffUtil.ItemCallback<TrendingMovie>() {
        override fun areItemsTheSame(oldItem: TrendingMovie, newItem: TrendingMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrendingMovie, newItem: TrendingMovie): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentLayoutMode
    }

    private companion object {
        const val VIEW_TYPE_GRID = 1
        const val VIEW_TYPE_LINEAR = 2
    }

    private var currentLayoutMode = VIEW_TYPE_GRID

}