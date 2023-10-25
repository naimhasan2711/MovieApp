package com.nakibul.android.movieapp.data.remote.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nakibul.android.movieapp.data.remote.datasource.api.MovieService
import com.nakibul.android.movieapp.data.remote.models.response.TrendingMovieResponse

class MoviePagingSource(private val apiService: MovieService) :
    PagingSource<Int, TrendingMovieResponse>() {
    override fun getRefreshKey(state: PagingState<Int, TrendingMovieResponse>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrendingMovieResponse> {
        return try {
            val position = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = apiService.fetchAllTrendingMovies(position)
            LoadResult.Page(
                data = response.results, prevKey = if (position == 1) null else position - 1,
                nextKey = position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

private const val TMDB_STARTING_PAGE_INDEX = 1