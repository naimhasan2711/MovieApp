package com.nakibul.android.movieapp.utils

object Constants {
    const val API_KEY = "YOUR_API_KEY"

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_URL_IMAGE_PATH = "https://image.tmdb.org/t/p/original"

    const val END_POINT_TRENDING_MOVIES = "trending/movie/day?api_key=$API_KEY"
    //https://api.themoviedb.org/3/trending/movie/day?api_key=5f2609088d5fd3e963ec0abf71735504
    const val END_POINT_MOVIE_DETAIL = "movie/"
    //https://api.themoviedb.org/3/movie/926393?api_key=5f2609088d5fd3e963ec0abf71735504
    const val END_POINT_MOVIE_CAST = "movie/{movie_id}/credits?api_key=$API_KEY"
   // https://api.themoviedb.org/3/movie/926393/credits?api_key=5f2609088d5fd3e963ec0abf71735504
    const val END_POINT_UPCOMING_MOVIES = "movie/upcoming"
    const val END_POINT_NOW_PLAYING_MOVIES = "movie/now_playing"
    const val END_POINT_SEARCH_MOVIE = "search/movie"
}