package com.smile.watchmovie.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.smile.watchmovie.adapter.PagingAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.ActivityShowMoreCategoryFilmBinding
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.CategoryFilmActivityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShowMoreCategoryFilmActivity : AppCompatActivity(), PagingAdapter.OnClickListener {
    private lateinit var binding: ActivityShowMoreCategoryFilmBinding
    private var categoryId = 0
    private lateinit var viewModel: CategoryFilmActivityViewModel
    private lateinit var pagingAdapter: PagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowMoreCategoryFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        setupToolBar()
        getCategoryFilmFromApi()
    }

    private fun getCategoryFilmFromApi() {
        lifecycleScope.launch {
            viewModel.getListByCategory(categoryId).collectLatest {
                pagingAdapter.submitData(it)
            }
        }

        pagingAdapter.addLoadStateListener {
            binding.progressBar.visibility = if (it.refresh == LoadState.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initializeData() {
        categoryId = intent.getIntExtra("categoryId", 0)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceFilm))
        )[CategoryFilmActivityViewModel::class.java]

        pagingAdapter = PagingAdapter(this, this)

        binding.rcvFilmCategory.apply {
            adapter = pagingAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupToolBar() {
        binding.toolBar.apply {
            title = when (categoryId) {
                14 -> "Hoạt hình"
                6 -> "Hành động"
                4 -> "Kinh dị"
                11 -> "Trinh thám"
                12 -> "Hài kịch"
                13 -> "Lãng mạn"
                else -> "Phiêu lưu"
            }

            setNavigationOnClickListener {
                finish()
            }
        }
    }

    override fun playFilm(film: FilmMainHome) {
        viewModel.getFilmDetail(Constant.Api.WS_TOKEN, film.id).observe(this) {
            it?.let {
                val intent = Intent(this, WatchFilmActivity::class.java)
                intent.putExtra("film", it.data)
                startActivity(intent)
            }
        }
    }
}