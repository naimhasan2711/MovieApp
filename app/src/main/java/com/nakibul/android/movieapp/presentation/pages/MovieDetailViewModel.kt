package com.nakibul.android.movieapp.presentation.pages


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.android.movieapp.data.remote.repository.MovieRemoteRepository
import com.nakibul.android.movieapp.domain.models.Genre
import com.nakibul.android.movieapp.domain.models.MovieCast
import com.nakibul.android.movieapp.domain.models.MovieDetail
import com.nakibul.android.movieapp.utils.Status
import com.nakibul.android.movieapp.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRemoteRepository: MovieRemoteRepository
) :
    ViewModel() {

    private var genres = mutableListOf<Genre>()
    private var movieCast = mutableListOf<MovieCast>()
    private var movieDetail = MovieDetail(
        id = 0,
        title = "",
        voteAverage = 0.0,
        originalLanguage = "",
        overview = "",
        backdropPath = "",
        genres = genres,
        posterPath = ""
    )

    private val movieDetailState = MutableStateFlow(
        ViewState(
            Status.LOADING,
            movieDetail,
            ""
        )
    )

    private val movieCastState = MutableStateFlow(
        ViewState(
            Status.LOADING,
            movieCast,
            ""
        )
    )

    fun getMovieDetailsById(id: Int) {
        fetchMovieCast(id)
        fetchMovieDetailsById(id)
    }

    private fun fetchMovieDetailsById(id: Int) {

        movieDetailState.value = ViewState.loading()

        viewModelScope.launch {
            movieRemoteRepository.fetchTrendingMovieDetailsData(id)
                .catch {
                    movieDetailState.value = ViewState.error(msg = it.message.toString())
                }
                .collect { trendingMovieDetailsViewState ->
                    trendingMovieDetailsViewState.data?.let { movieDetailsResponse ->
                        genres = movieDetailsResponse.genreResponses!!.map { genreResponse ->
                            Genre(
                                id = genreResponse.id!!,
                                name = genreResponse.name!!
                            )
                        }.toMutableList()

                        movieDetail = MovieDetail(
                            title = movieDetailsResponse.title!!,
                            id = movieDetailsResponse.id!!,
                            voteAverage = movieDetailsResponse.voteAverage!!,
                            originalLanguage = movieDetailsResponse.originalLanguage!!,
                            backdropPath = movieDetailsResponse.backdropPath!!,
                            overview = movieDetailsResponse.overview!!,
                            genres = genres,
                            posterPath = movieDetailsResponse.posterPath!!
                        )

                        movieDetailState.value = ViewState.success(data = movieDetail)
                    }

                }
        }
    }

    private fun fetchMovieCast(id: Int) {
        movieCastState.value = ViewState.loading()

        viewModelScope.launch {
            movieRemoteRepository.fetchMovieCast(id = id)
                .catch {
                    movieCastState.value = ViewState.error(msg = it.message.toString())
                }
                .collect { movieCastViewState ->
                    movieCastViewState.data?.let { castResponse ->
                        movieCast = castResponse.cast!!.map {

                            MovieCast(
                                id = it.id!!,
                                name = it.name!!,
                                gender = it.gender!!,
                                profilePath = it.profilePath,
                                character = it.character!!,
                            )

                        }.toMutableList()
                        movieCastState.value = ViewState.success(data = movieCast)
                    }
                }

        }
    }

}
