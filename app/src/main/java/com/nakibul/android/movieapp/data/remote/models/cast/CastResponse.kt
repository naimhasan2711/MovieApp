package com.nakibul.android.movieapp.data.remote.models.cast

import com.google.gson.annotations.SerializedName

data class CastResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("cast")
    val cast: List<SingleCastResponse>? = null,
)
