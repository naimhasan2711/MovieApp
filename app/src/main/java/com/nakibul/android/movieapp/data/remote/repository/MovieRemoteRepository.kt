package com.nakibul.android.movieapp.data.remote.repository

import android.util.Log
import android.view.View
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nakibul.android.movieapp.data.remote.datasource.api.MovieService
import com.nakibul.android.movieapp.data.remote.datasource.paging.MoviePagingSource
import com.nakibul.android.movieapp.data.remote.models.cast.CastResponse
import com.nakibul.android.movieapp.data.remote.models.now_playing_movies.NowPlayingMovieListResponse
import com.nakibul.android.movieapp.data.remote.models.response.MovieDetailResponse
import com.nakibul.android.movieapp.data.remote.models.response.TrendingMovieListResponse
import com.nakibul.android.movieapp.data.remote.models.response.TrendingMovieResponse
import com.nakibul.android.movieapp.data.remote.models.upcoming_movies.UpcomingMovieListResponse
import com.nakibul.android.movieapp.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRemoteRepository @Inject constructor(
    private val movieService: MovieService
) {
    suspend fun fetchTrendingMovieList(): Flow<ViewState<TrendingMovieListResponse>> {
        return flow {
            val trendingMovieListResponse = movieService.fetchAllTrendingMovies(1)
            emit(ViewState.success(trendingMovieListResponse))
        }.flowOn(Dispatchers.IO)
    }

    fun getPagerMovies(): Flow<PagingData<TrendingMovieResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = { MoviePagingSource(movieService) }
        ).flow.flowOn(Dispatchers.IO)
    }

    suspend fun fetchTrendingMovieDetailsData(id: Int): Flow<ViewState<MovieDetailResponse>> {
        return flow {
            val trendingMovieDetailsData = movieService.fetchMovieDetailsById(id)
            Log.i("MovieRepository", "movieDetailResponse : $trendingMovieDetailsData")
            emit(ViewState.success(trendingMovieDetailsData))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchMovieCast(id: Int): Flow<ViewState<CastResponse>> {
        return flow {
            val castResponse = movieService.fetchMovieCast(id)
            emit(ViewState.success(castResponse))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchUpcomingMovies(): Flow<ViewState<UpcomingMovieListResponse>> {
        return flow {
            val upcomingMoviesResponse = movieService.fetchUpcomingMovies()

            emit(ViewState.success(upcomingMoviesResponse))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchNowPlayingMovies(): Flow<ViewState<NowPlayingMovieListResponse>> {
        return flow {
            val nowPlayingMovieListResponse = movieService.fetchNowPlayingMovies()

            emit(ViewState.success(nowPlayingMovieListResponse))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchMovie(queryText: String): Flow<ViewState<TrendingMovieListResponse>> {
        return flow {
            val searchedTrendingMovieResponse = movieService.searchMovie(queryText)
            emit(ViewState.success(searchedTrendingMovieResponse))
        }.flowOn(Dispatchers.IO)
    }


}