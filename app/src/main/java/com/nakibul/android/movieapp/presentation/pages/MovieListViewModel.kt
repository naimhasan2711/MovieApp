package com.nakibul.android.movieapp.presentation.pages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.nakibul.android.movieapp.data.remote.repository.MovieRemoteRepository
import com.nakibul.android.movieapp.domain.models.NowPlayingMovie
import com.nakibul.android.movieapp.domain.models.TrendingMovie
import com.nakibul.android.movieapp.domain.models.UpcomingMovie
import com.nakibul.android.movieapp.utils.PagerMovieListState
import com.nakibul.android.movieapp.utils.Status
import com.nakibul.android.movieapp.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRemoteRepository: MovieRemoteRepository
) : ViewModel() {

    private var searchedTrendingMovieList = mutableListOf<TrendingMovie>()
    private var upComingMovieList = mutableListOf<UpcomingMovie>()
    private var nowPlayingMovieList = mutableListOf<NowPlayingMovie>()
    private var savedMovieList = mutableListOf<TrendingMovie>()

    private val _pager_movieListState =
        MutableStateFlow<PagerMovieListState>(PagerMovieListState.Loading)
    val pagerMovieListState: StateFlow<PagerMovieListState> = _pager_movieListState

    val nowPlayingMovieListState = MutableStateFlow(
        ViewState(
            Status.LOADING, nowPlayingMovieList, ""
        )
    )

    val upcomingMovieListState = MutableStateFlow(
        ViewState(
            Status.LOADING, upComingMovieList, ""
        )
    )

    val searchedMovieListState = MutableStateFlow(
        ViewState(
            Status.LOADING, searchedTrendingMovieList, ""
        )
    )

    val savedMovieListState = MutableStateFlow(
        ViewState(
            Status.LOADING, savedMovieList, "",
        )
    )

    fun loadTrendingMovieList() {
        _pager_movieListState.value = PagerMovieListState.Loading
        viewModelScope.launch {
            val pagerFlow = movieRemoteRepository.getPagerMovies()
                .catch {
                    Log.i("error", "loadTrendingMovieList Error")
                    _pager_movieListState.value = PagerMovieListState.Error(it)
                }
                .map { pagingData ->
                    pagingData.map {
                        Log.i(
                            "aaa",
                            "trendingMovieResponse : ${it.backdropPath}"
                        )
                        TrendingMovie(
                            id = it.id!!,
                            title = it.title!!,
                            voteAverage = it.voteAverage!!,
                            originalLanguage = it.originalLanguage!!,
                            backdropPath = it.backdropPath,
                            posterPath = it.posterPath,
                            overview = it.overview!!,
                            isSaved = false
                        )
                    }
                }
                .cachedIn(viewModelScope)
            _pager_movieListState.value = PagerMovieListState.Success(pagerFlow)
        }
    }


    suspend fun searchedMovie(queryText: String) {
        searchedMovieListState.value = ViewState.loading()

        viewModelScope.launch {
            movieRemoteRepository.searchMovie(queryText)
                .catch {
                    searchedMovieListState.value = ViewState.error(it.message.toString())
                }
                .collect { it2 ->
                    it2.data?.let { it3 ->
                        searchedTrendingMovieList =
                            it3.results.map {
                                TrendingMovie(
                                    id = it.id!!,
                                    title = it.title!!,
                                    voteAverage = it.voteAverage!!,
                                    originalLanguage = it.originalLanguage!!,
                                    backdropPath = it.backdropPath,
                                    posterPath = it.posterPath,
                                    overview = it.overview!!,
                                    isSaved = false
                                )
                            }.toMutableList()

                        searchedMovieListState.value = ViewState.success(searchedTrendingMovieList)
                        Log.i("MovieListFragment", "Received searched list.")
                    }
                        ?: run {
                            Log.e("MovieListFragment", "Error: Failed to fetch searched list.")
                        }
                }
        }
    }

    fun getAllUpComingMovies() {
        upcomingMovieListState.value = ViewState.loading()
        viewModelScope.launch {
            movieRemoteRepository.fetchUpcomingMovies().catch {
                upcomingMovieListState.value = ViewState.error(it.message.toString())
            }
                .collect { it1 ->
                    it1.data?.let { it2 ->
                        upComingMovieList = it2.results!!.map {
                            UpcomingMovie(
                                id = it.id!!,
                                poster = it.poster_path!!
                            )
                        }.toMutableList()
                        upcomingMovieListState.value = ViewState.success(data = upComingMovieList)
                    }

                }
        }
    }

    fun getAllNowPlayingMovies() {

        nowPlayingMovieListState.value = ViewState.loading()

        viewModelScope.launch {
            movieRemoteRepository.fetchNowPlayingMovies()
                .catch {
                    nowPlayingMovieListState.value =
                        ViewState.error(it.message.toString())
                }
                .collect { nowPlayingMovieListViewState ->

                    nowPlayingMovieListViewState.data?.let { nowPlayingMovieListResponse ->

                        nowPlayingMovieList =
                            nowPlayingMovieListResponse.results!!.map { nowPlaingMovieResponse ->

                                NowPlayingMovie(
                                    id = nowPlaingMovieResponse.id!!,
                                    poster = nowPlaingMovieResponse.poster_path!!,
                                )

                            }.toMutableList()

                        nowPlayingMovieListState.value =
                            ViewState.success(data = nowPlayingMovieList)
                    }
                }
        }
    }

}