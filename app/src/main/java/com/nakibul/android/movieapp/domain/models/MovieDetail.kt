package com.nakibul.android.movieapp.domain.models

import com.nakibul.android.movieapp.domain.models.Genre

data class MovieDetail(

    val id: Int,
    val title: String,
    val voteAverage: Double,
    val originalLanguage: String,
    val overview: String,
    val backdropPath: String,
    val genres : List<Genre>,
    val posterPath: String,
)