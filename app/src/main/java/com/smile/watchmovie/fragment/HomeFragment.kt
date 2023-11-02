package com.smile.watchmovie.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smile.watchmovie.activity.ShowMoreCategoryFilmActivity
import com.smile.watchmovie.activity.WatchFilmActivity
import com.smile.watchmovie.adapter.HomeFragmentAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.FragmentHomeBinding
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter1: HomeFragmentAdapter
    private lateinit var adapter2: HomeFragmentAdapter
    private lateinit var adapter3: HomeFragmentAdapter
    private lateinit var adapter4: HomeFragmentAdapter
    private lateinit var adapter5: HomeFragmentAdapter
    private lateinit var adapter6: HomeFragmentAdapter
    private lateinit var adapter7: HomeFragmentAdapter
    private lateinit var viewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeData()
        getDataFromApi()
        handleEventClick()

        return binding.root
    }

    private fun handleEventClick() {
        binding.apply {
            moreCartonMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 14)
                startActivity(intent)
            }

            moreActionMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 6)
                startActivity(intent)
            }

            moreHorrorMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 4)
                startActivity(intent)
            }

            moreThrillerMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 11)
                startActivity(intent)
            }

            moreComedyMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 12)
                startActivity(intent)
            }

            moreRomanticMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 13)
                startActivity(intent)
            }

            moreAdventureMovie.setOnClickListener {
                val intent = Intent(requireActivity(), ShowMoreCategoryFilmActivity::class.java)
                intent.putExtra("categoryId", 15)
                startActivity(intent)
            }
        }
    }

    private fun getDataFromApi() {
        viewModel.apply {
            getFilmByCategory(Constant.Api.WS_TOKEN, 14, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter1.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 6, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter2.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 4, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter3.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 11, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter4.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 12, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter5.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 13, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter6.setData(it.data)
                }
            }

            getFilmByCategory(Constant.Api.WS_TOKEN, 15, 0, 5).observe(requireActivity()) {
                it?.let {
                    adapter7.setData(it.data)
                }
            }
        }
    }

    private fun initializeData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceFilm))
        )[HomeFragmentViewModel::class.java]

        adapter1 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter2 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter3 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter4 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter5 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter6 =
            HomeFragmentAdapter(requireActivity(), this)
        adapter7 =
            HomeFragmentAdapter(requireActivity(), this)

        binding.apply {
            rcvFilm1.apply {
                setHasFixedSize(true)
                adapter = adapter1
            }

            rcvFilm2.apply {
                setHasFixedSize(true)
                adapter = adapter2
            }

            rcvFilm3.apply {
                setHasFixedSize(true)
                adapter = adapter3
            }

            rcvFilm4.apply {
                setHasFixedSize(true)
                adapter = adapter4
            }

            rcvFilm5.apply {
                setHasFixedSize(true)
                adapter = adapter5
            }

            rcvFilm6.apply {
                setHasFixedSize(true)
                adapter = adapter6
            }

            rcvFilm7.apply {
                setHasFixedSize(true)
                adapter = adapter7
            }
        }
    }

    override fun openFilm(filmId: Int) {
        viewModel.getFilmDetail(Constant.Api.WS_TOKEN, filmId).observe(requireActivity()) {
            it?.let {
                val intent = Intent(requireActivity(), WatchFilmActivity::class.java)
                intent.putExtra("film", it.data)
                startActivity(intent)
            }
        }
    }
}