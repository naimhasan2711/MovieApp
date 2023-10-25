package com.nakibul.android.movieapp.data.remote.models.now_playing_movies

import com.google.gson.annotations.SerializedName

data class NowPlayingMovieResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("poster_path")
    val poster_path: String? = null,
)
