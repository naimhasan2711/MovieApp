package com.nakibul.android.movieapp.data.remote.models.response


import com.google.gson.annotations.SerializedName

data class TrendingMovieListResponse(

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<TrendingMovieResponse>,
)