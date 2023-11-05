package com.smile.watchmovie.page

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.repository.ApiRepository
import com.smile.watchmovie.utils.Constant

class CategoryFilmPagingSource(
    private val apiRepository: ApiRepository,
    private val categoryId: Int
) : PagingSource<Int, FilmMainHome>() {
    override fun getRefreshKey(state: PagingState<Int, FilmMainHome>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmMainHome> {
        return try {
            val nextPage = params.key ?: 0
            val response =
                apiRepository.getFilmByCategory(Constant.Api.WS_TOKEN, categoryId, nextPage, 10)
            LoadResult.Page(
                data = response.data,
                prevKey = if (nextPage == 0) null else nextPage - 1,
                nextKey = if (nextPage == 18) null else nextPage + 1
            )
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message.toString())
            LoadResult.Error(e)
        }
    }
}