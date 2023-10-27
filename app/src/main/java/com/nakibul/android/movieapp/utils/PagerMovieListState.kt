package com.nakibul.android.movieapp.utils

import androidx.paging.PagingData
import com.nakibul.android.movieapp.domain.models.TrendingMovie
import kotlinx.coroutines.flow.Flow

sealed class PagerMovieListState{
    object Loading: PagerMovieListState()

    data class Success(val pagingData: Flow<PagingData<TrendingMovie>>): PagerMovieListState()

    data class Error(val error:Throwable) : PagerMovieListState()
}
