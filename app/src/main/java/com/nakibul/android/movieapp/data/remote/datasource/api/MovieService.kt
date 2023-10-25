package com.nakibul.android.movieapp.data.remote.datasource.api

import com.nakibul.android.movieapp.data.remote.models.cast.CastResponse
import com.nakibul.android.movieapp.data.remote.models.now_playing_movies.NowPlayingMovieListResponse
import com.nakibul.android.movieapp.data.remote.models.response.MovieDetailResponse
import com.nakibul.android.movieapp.data.remote.models.response.TrendingMovieListResponse
import com.nakibul.android.movieapp.data.remote.models.upcoming_movies.UpcomingMovieListResponse
import com.nakibul.android.movieapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET(Constants.END_POINT_TRENDING_MOVIES)
    suspend fun fetchAllTrendingMovies(@Query("page") page: Int): TrendingMovieListResponse

    @GET("${Constants.END_POINT_MOVIE_DETAIL}{movie_id}")
    suspend fun fetchMovieDetailsById(@Path("movie_id") movieId: Int): MovieDetailResponse

    @GET(Constants.END_POINT_MOVIE_CAST)
    suspend fun fetchMovieCast(@Path("movie_id") movieId: Int): CastResponse

    @GET(Constants.END_POINT_UPCOMING_MOVIES)
    suspend fun fetchUpcomingMovies(): UpcomingMovieListResponse

    @GET(Constants.END_POINT_NOW_PLAYING_MOVIES)
    suspend fun fetchNowPlayingMovies(): NowPlayingMovieListResponse

    @GET(Constants.END_POINT_SEARCH_MOVIE)
    suspend fun searchMovie(@Query("query") queryText: String): TrendingMovieListResponse
}