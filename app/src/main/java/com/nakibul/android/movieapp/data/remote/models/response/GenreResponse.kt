package com.nakibul.android.movieapp.data.remote.models.response

import com.google.gson.annotations.SerializedName

data class GenreResponse(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,
)