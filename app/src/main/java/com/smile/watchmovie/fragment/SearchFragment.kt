package com.smile.watchmovie.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.smile.watchmovie.activity.PlayerActivity
import com.smile.watchmovie.adapter.PagingAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.FragmentSearchBinding
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.SearchFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), PagingAdapter.OnClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchFragmentViewModel
    private lateinit var pagingAdapter: PagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initializeData()
        handleEvent()

        return binding.root
    }

    private fun initializeData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceFilm))
        )[SearchFragmentViewModel::class.java]

        pagingAdapter = PagingAdapter(requireActivity(), this)

        binding.apply {
            searchView.clearFocus()

            rcvFilm.apply {
                adapter = pagingAdapter
                setHasFixedSize(true)
            }
        }
    }

    private fun handleEvent() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.trim().isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.getListFilm(query).collectLatest {
                            pagingAdapter.submitData(it)
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        pagingAdapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading) {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    tvTitle.visibility = View.GONE
                }
            } else if (it.prepend.endOfPaginationReached && pagingAdapter.itemCount < 1) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    tvTitle.visibility = View.VISIBLE
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun playFilm(film: FilmMainHome) {
        viewModel.getFilmDetail(Constant.Api.WS_TOKEN, film.id).observe(requireActivity()) {
            it?.let {
                val intent = Intent(requireActivity(), PlayerActivity::class.java)
                intent.putExtra("film", it.data)
                startActivity(intent)
            }
        }
    }
}