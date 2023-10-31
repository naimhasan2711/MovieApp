package com.nakibul.android.movieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nakibul.android.movieapp.databinding.FragmentMovieDetailBinding
import com.nakibul.android.movieapp.databinding.FragmentMovieListBinding
import com.nakibul.android.movieapp.presentation.pages.MovieDetailViewModel


class MovieDetailFragment : Fragment() {

    private lateinit var binding :FragmentMovieListBinding
    private val movieDetailsViewModel: MovieDetailViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieId = requireArguments().getInt(BUNDLE_KEY_MOVIE_ID)
        movieDetailsViewModel.getMovieDetailsById(id)
        observeData()
    }

    private fun observeData() {
        TODO("Not yet implemented")
    }


    companion object {
        const val BUNDLE_KEY_MOVIE_ID = "movie_id"
    }
}