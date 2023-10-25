package com.nakibul.android.movieapp.data.remote.models.upcoming_movies

import com.google.gson.annotations.SerializedName

data class UpcomingMovieResponse(

    @SerializedName("id")
    val id: Int?,

    @SerializedName("poster_path")
    val poster_path: String? = null,
)