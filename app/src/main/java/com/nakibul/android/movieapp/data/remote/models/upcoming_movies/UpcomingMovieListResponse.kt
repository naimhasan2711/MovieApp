package com.nakibul.android.movieapp.data.remote.models.upcoming_movies

import com.google.gson.annotations.SerializedName

data class UpcomingMovieListResponse(

    @SerializedName("dates")
    val dates: DatesResponse? = null,

    @SerializedName("results")
    val results: List<UpcomingMovieResponse>? = null,
)