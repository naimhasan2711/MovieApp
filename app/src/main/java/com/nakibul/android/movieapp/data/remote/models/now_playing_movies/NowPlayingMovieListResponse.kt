package com.nakibul.android.movieapp.data.remote.models.now_playing_movies

import com.google.gson.annotations.SerializedName

data class NowPlayingMovieListResponse(
    @SerializedName("dates")
    val dates: DatesResponse? = null,

    @SerializedName("results")
    val results: List<NowPlayingMovieResponse>? = null,
)