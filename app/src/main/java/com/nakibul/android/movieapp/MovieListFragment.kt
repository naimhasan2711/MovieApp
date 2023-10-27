package com.nakibul.android.movieapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakibul.android.movieapp.databinding.FragmentMovieListBinding
import com.nakibul.android.movieapp.presentation.adapters.NowPlayingMoviesAdapter
import com.nakibul.android.movieapp.presentation.adapters.UpcomingMoviesAdapter
import com.nakibul.android.movieapp.presentation.pages.MovieListViewModel
import com.nakibul.android.movieapp.utils.NetworkUtils
import com.nakibul.android.movieapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private val movieListViewModel: MovieListViewModel by viewModels()

    private lateinit var upcomingMoviesAdapter: UpcomingMoviesAdapter
    private lateinit var nowPlayingMoviesAdapter: NowPlayingMoviesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInit()
        setConnection()
        observeData()
    }

    private fun setConnection() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            onConnectionAvailable()
        } else {
            onConnectionUnavailable()
        }
    }

    private fun onConnectionAvailable() {
        movieListViewModel.apply {
            getAllUpComingMovies()
            getAllNowPlayingMovies()
        }
        binding.apply {
            upcomingMoviesLayout.root.visibility = View.VISIBLE
            upcomingMoviesListTitle.visibility = View.VISIBLE

            nowPlayingMoviesLayout.root.visibility = View.VISIBLE
            nowPlayingMovieListTitle.visibility = View.VISIBLE
        }
    }

    private fun onConnectionUnavailable() {
        binding.apply {
            upcomingMoviesLayout.root.visibility = View.GONE
            upcomingMoviesListTitle.visibility = View.GONE

            nowPlayingMoviesLayout.root.visibility = View.GONE
            nowPlayingMovieListTitle.visibility = View.GONE


        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.upcomingMovieListState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.upcomingMoviesLayout.progressBarUpcomingMovies.visibility =
                            View.VISIBLE
                        binding.upcomingMoviesLayout.root.visibility = View.GONE

                    }

                    Status.SUCCESS -> {
                        binding.upcomingMoviesLayout.progressBarUpcomingMovies.visibility =
                            View.GONE
                        binding.upcomingMoviesLayout.root.visibility = View.VISIBLE

                        it.data?.let { upcomingMovies ->

                            upcomingMoviesAdapter = UpcomingMoviesAdapter(
                                upcomingMovies = upcomingMovies,
                            ) { upcomingMovie ->
                                onMovieItemClicked(upcomingMovie.id)
                            }

                            binding.upcomingMoviesLayout.upcomingMoviesRecyclerView.adapter =
                                upcomingMoviesAdapter

                        }
                    }

                    Status.ERROR -> {
                        Log.i("MovieListFragment", "Upcoming Error")
                        binding.upcomingMoviesLayout.progressBarUpcomingMovies.visibility =
                            View.GONE
                        binding.upcomingMoviesListTitle.visibility = View.GONE

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.nowPlayingMovieListState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.nowPlayingMoviesLayout.progressBarNowPlayingMovies.visibility =
                            View.VISIBLE
                        binding.nowPlayingMoviesLayout.nowPlayingMoviesRecyclerView.visibility =
                            View.GONE
                    }

                    Status.SUCCESS -> {
                        binding.nowPlayingMoviesLayout.progressBarNowPlayingMovies.visibility =
                            View.GONE
                        binding.nowPlayingMoviesLayout.nowPlayingMoviesRecyclerView.visibility =
                            View.VISIBLE

                        it.data?.let { nowPlayingMovies ->
                            nowPlayingMoviesAdapter =
                                NowPlayingMoviesAdapter(nowPlayingMovies) { nowPlayingMovie ->
                                    onMovieItemClicked(nowPlayingMovie.id)
                                }
                        }
                        binding.nowPlayingMoviesLayout.nowPlayingMoviesRecyclerView.adapter =
                            nowPlayingMoviesAdapter
                    }

                    Status.ERROR -> {
                        binding.nowPlayingMoviesLayout.progressBarNowPlayingMovies.visibility =
                            View.GONE
                        binding.nowPlayingMoviesLayout.nowPlayingMoviesRecyclerView.visibility =
                            View.GONE
                        binding.upcomingMoviesListTitle.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun onMovieItemClicked(movieId: Int) {

        val bundle =
            bundleOf(MovieDetailFragment.BUNDLE_KEY_MOVIE_ID to movieId)
        view?.findNavController()
            ?.navigate(
                R.id.action_movieListFragment_to_movieDetailFragment,
                bundle
            )
    }

    private fun setInit() {

        binding.apply {

            upcomingMoviesLayout.upcomingMoviesRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            nowPlayingMoviesLayout.nowPlayingMoviesRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        }

    }

}