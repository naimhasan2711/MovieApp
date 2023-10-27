package com.nakibul.android.movieapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakibul.android.movieapp.databinding.FragmentMovieListBinding
import com.nakibul.android.movieapp.presentation.adapters.MoviesPagerAdapter
import com.nakibul.android.movieapp.presentation.adapters.NowPlayingMoviesAdapter
import com.nakibul.android.movieapp.presentation.adapters.SearchMovieAdapter
import com.nakibul.android.movieapp.presentation.adapters.UpcomingMoviesAdapter
import com.nakibul.android.movieapp.presentation.pages.MovieListViewModel
import com.nakibul.android.movieapp.utils.NetworkUtils
import com.nakibul.android.movieapp.utils.PagerMovieListState
import com.nakibul.android.movieapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding

    private val movieListViewModel: MovieListViewModel by viewModels()

    private lateinit var upcomingMoviesAdapter: UpcomingMoviesAdapter
    private lateinit var nowPlayingMoviesAdapter: NowPlayingMoviesAdapter

    private lateinit var moviesPagerAdapter: MoviesPagerAdapter
    private lateinit var searchMovieAdapter: SearchMovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInit()
        setOnClickListeners()
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

    private fun retryButtonClick() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            onConnectionAvailable()
        } else {
            onConnectionUnavailable()
        }
    }

    private fun setOnClickListeners() {
        binding.btnRetry.setOnClickListener {
            retryButtonClick()
        }

        binding.customToolbar.changeLayoutBtn.setOnClickListener {
            changeLayout()
        }

        binding.customToolbar.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                setViewsInSearching()
            } else {
                setViewsAfterSearching()
                binding.trendingMoviesRecyclerView.adapter = moviesPagerAdapter
            }
        }

        binding.customToolbar.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                viewLifecycleOwner.lifecycleScope.launch {
                    movieListViewModel.searchedMovie(queryText = newText!!)
                }

                return true
            }

        })
    }

    private fun setViewsAfterSearching() {
        binding.upcomingMoviesLayout.root.visibility = View.VISIBLE
        binding.upcomingMoviesListTitle.visibility = View.VISIBLE

        binding.nowPlayingMoviesLayout.root.visibility = View.VISIBLE
        binding.nowPlayingMovieListTitle.visibility = View.VISIBLE


        val nowPlayingLayout: View = requireActivity().findViewById(R.id.nowPlayingMoviesLayout)
        val params = binding.trendinggMoviesListTitle.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = nowPlayingLayout.id
        params.setMargins(10, 40, 10, 10)
        binding.trendinggMoviesListTitle.requestLayout()
    }

    private fun setViewsInSearching() {
        binding.upcomingMoviesLayout.root.visibility = View.GONE
        binding.upcomingMoviesListTitle.visibility = View.GONE

        binding.nowPlayingMoviesLayout.root.visibility = View.GONE
        binding.nowPlayingMovieListTitle.visibility = View.GONE

        val toolbar: View = requireActivity().findViewById(R.id.customToolbar)

        val params = binding.trendinggMoviesListTitle.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = toolbar.id
        params.setMargins(10, 10, 10, 10)
        binding.trendinggMoviesListTitle.requestLayout()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeLayout() {
        val layoutManager = binding.trendingMoviesRecyclerView.layoutManager

        if (layoutManager is GridLayoutManager) {
            binding.trendingMoviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            moviesPagerAdapter.switchToGridMode()
            moviesPagerAdapter.notifyDataSetChanged()
            binding.customToolbar.changeLayoutBtn.setBackgroundResource(R.drawable.ic_grid_view)
        } else if (layoutManager is LinearLayoutManager) {
            binding.trendingMoviesRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2)
            moviesPagerAdapter.switchToLinearMode()
            moviesPagerAdapter.notifyDataSetChanged()
            binding.customToolbar.changeLayoutBtn.setBackgroundResource(R.drawable.ic_linear_view)
        }
    }

    private fun onConnectionAvailable() {
        movieListViewModel.apply {
            getAllUpComingMovies()
            getAllNowPlayingMovies()
            loadTrendingMovieList()
        }
        binding.apply {
            upcomingMoviesLayout.root.visibility = View.VISIBLE
            upcomingMoviesListTitle.visibility = View.VISIBLE

            nowPlayingMoviesLayout.root.visibility = View.VISIBLE
            nowPlayingMovieListTitle.visibility = View.VISIBLE

            btnRetry.visibility = View.GONE
            tvError.visibility = View.GONE

            progressBarTrendingMovies.visibility = View.GONE
            trendingMoviesRecyclerView.visibility = View.VISIBLE
            trendinggMoviesListTitle.visibility = View.VISIBLE
        }
    }

    private fun onConnectionUnavailable() {
        binding.apply {
            upcomingMoviesLayout.root.visibility = View.GONE
            upcomingMoviesListTitle.visibility = View.GONE

            nowPlayingMoviesLayout.root.visibility = View.GONE
            nowPlayingMovieListTitle.visibility = View.GONE

            btnRetry.visibility = View.VISIBLE
            tvError.visibility = View.VISIBLE

            progressBarTrendingMovies.visibility = View.GONE
            trendingMoviesRecyclerView.visibility = View.GONE
            trendinggMoviesListTitle.visibility = View.GONE

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

        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.pagerMovieListState.collect { state ->

                when (state) {
                    is PagerMovieListState.Loading -> {
                        binding.progressBarTrendingMovies.visibility = View.VISIBLE
                        binding.trendingMoviesRecyclerView.visibility = View.GONE
                        binding.btnRetry.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                    }

                    is PagerMovieListState.Success -> {
                        binding.progressBarTrendingMovies.visibility = View.GONE
                        binding.trendingMoviesRecyclerView.visibility = View.VISIBLE
                        binding.btnRetry.visibility = View.GONE
                        binding.tvError.visibility = View.GONE

                        state.pagingData.collectLatest {
                            moviesPagerAdapter.submitData(it)
                        }
                    }

                    is PagerMovieListState.Error -> {
                        binding.progressBarTrendingMovies.visibility = View.GONE
                        binding.trendingMoviesRecyclerView.visibility = View.GONE
                        binding.btnRetry.visibility = View.VISIBLE
                        binding.tvError.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.searchedMovieListState.collect {
                when (it.status) {

                    Status.LOADING -> {
                        binding.progressBarTrendingMovies.visibility = View.VISIBLE
                        binding.trendingMoviesRecyclerView.visibility = View.GONE
                    }

                    Status.SUCCESS -> {
                        binding.progressBarTrendingMovies.visibility = View.GONE
                        binding.trendingMoviesRecyclerView.visibility = View.VISIBLE

                        it.data.let { searchedMovieList ->
                            searchMovieAdapter = SearchMovieAdapter(
                                movies = searchedMovieList!!,
                                onSaveButtonClickListener = { trendingMovie ->

                                },
                                onItemClickListener = { trendingMovie ->
                                    onMovieItemClicked(movieId = trendingMovie.id)
                                })

                            binding.trendingMoviesRecyclerView.adapter = searchMovieAdapter
                        }
                    }

                    Status.ERROR -> {

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
            trendingMoviesRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
            }

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

        moviesPagerAdapter = MoviesPagerAdapter(onItemClickListener = {
            onMovieItemClicked(movieId = it.id)
        },
            onSaveButtonClickListener = {

            }
        )

        binding.trendingMoviesRecyclerView.adapter = moviesPagerAdapter

    }

    override fun onResume() {
        super.onResume()
        setConnection()
        binding.trendingMoviesRecyclerView.adapter = moviesPagerAdapter
    }

}