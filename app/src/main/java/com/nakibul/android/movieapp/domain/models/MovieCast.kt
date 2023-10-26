package com.nakibul.android.movieapp.domain.models

data class MovieCast(

    val id: Int,

    val name: String,

    val gender: Int,

    val profilePath: String? = null,

    val character: String,
)