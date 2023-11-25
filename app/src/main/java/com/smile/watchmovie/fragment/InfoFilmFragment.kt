package com.smile.watchmovie.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.smile.watchmovie.R
import com.smile.watchmovie.activity.PlayerActivity
import com.smile.watchmovie.adapter.EpisodeAdapter
import com.smile.watchmovie.adapter.EpisodeAdapter.OnListener
import com.smile.watchmovie.adapter.FilmRelativeAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.ApiService
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.FragmentIntroduceFilmBinding
import com.smile.watchmovie.dialog.InfoFilmDialog
import com.smile.watchmovie.model.FilmArrayResponse
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.model.FilmReaction
import com.smile.watchmovie.model.SubFilm
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.InfoFilmFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class InfoFilmFragment : Fragment(), OnListener, FilmRelativeAdapter.OnClickListener {
    private lateinit var binding: FragmentIntroduceFilmBinding
    private lateinit var activity: PlayerActivity
    private lateinit var filmRelativeAdapter: FilmRelativeAdapter
    private lateinit var viewModel: InfoFilmFragmentViewModel
    private lateinit var filmMainHome: FilmMainHome
    private lateinit var mEpisodeAdapter: EpisodeAdapter
    private lateinit var documentReferenceFilmFavorite: DocumentReference
    private lateinit var documentReferenceLikeOrDislike: DocumentReference
    private var statusFavorite = 0
    private lateinit var subFilmArrayList: List<SubFilm>
    private lateinit var documentIdFilmFavorite: String
    private var documentIdLikeDislike = ""
    private var totalLike = 0
    private var totalDislike = 0
    private var myReaction = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false)

        activity = requireActivity() as PlayerActivity
        initializeData()
        if (activity.idUser != "") {
            setUpFireBase()
        }
        handleEventClick()

        return binding.root
    }

    private fun initialFilmRelate() {
        callApiGetByCategoryListMovie(filmMainHome.categoryId, (0..5).random())
    }

    private fun handleEventClick() {
        binding.apply {
            /* Nhấn tải phim */
            loutDownload.setOnClickListener {
                Toast.makeText(activity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT)
                    .show()
            }

            /* Bắt sự kiện người dùng nhấn xem mô tả phim */
            loutIntro.setOnClickListener { clickOpenDetailFilm() }

            /* Nhấn yêu thích */
            loutFavorite.setOnClickListener {
                if (activity.idUser == "") {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.not_logged_in_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    updateFavorite()
                }
            }

            /* Nhấn like */
            loutLike.setOnClickListener {
                if (activity.idUser == "") {
                    Toast.makeText(
                        requireActivity(),
                        R.string.not_logged_in_message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setupLikeDislike(1)
                }
            }

            /* Nhấn dislike */
            loutDislike.setOnClickListener {
                if (activity.idUser == "") {
                    Toast.makeText(
                        requireActivity(),
                        R.string.not_logged_in_message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setupLikeDislike(2)
                }
            }
        }
    }

    private fun initializeData() {
        filmMainHome = activity.film
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceFilm))
        )[InfoFilmFragmentViewModel::class.java]

        filmRelativeAdapter = FilmRelativeAdapter(requireActivity(), this)
        mEpisodeAdapter = EpisodeAdapter(this)

        binding.apply {
            rcvMore.apply {
                adapter = filmRelativeAdapter
                setHasFixedSize(true)
            }

            rcvEpisode.apply {
                adapter = mEpisodeAdapter
                setHasFixedSize(true)
            }
        }

        /* Hiển thị các thông tin liên quan đến bộ phim */
        showInfo()
    }

    private fun showInfo() {
        binding.apply {
            /* Hiển thị thông tin cơ bản về phim */
            tvNameFilm.text = filmMainHome.name
            tvViewNumber.text = activity.getString(R.string.tv_view_number, filmMainHome.viewNumber)
        }

        /* Hiển thị các bộ phim liên quan */
        initialFilmRelate()

        /* Hiển thị số tập phim của phim đang chiếu */
        showEpisodesTotal()
    }

    private fun showEpisodesTotal() {
        if (filmMainHome.subVideoList != null) {
            subFilmArrayList = filmMainHome.subVideoList
            Collections.sort(subFilmArrayList)
            subFilmArrayList[0].setWatching(true)

            mEpisodeAdapter.setData(subFilmArrayList)
            binding.loutEpisode.visibility = View.VISIBLE
        } else {
            binding.loutEpisode.visibility = View.GONE
        }
    }

    private fun clickOpenDetailFilm() {
        val dialog = InfoFilmDialog(filmMainHome, requireActivity())
        dialog.show(activity.supportFragmentManager, InfoFilmDialog.TAG)
    }

    private fun setUpFireBase() {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        documentReferenceFilmFavorite = firebaseFirestore.document("WatchFilm/tblfilmfavorite")
        documentReferenceLikeOrDislike = firebaseFirestore.document("WatchFilm/tblfilmlike")
        loadFavorite()
        loadLikeAndDislike()
    }

    private fun loadLikeAndDislike() {
        documentReferenceLikeOrDislike
            .collection(filmMainHome.id.toString())
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    for (i in 0 until it.documents.size) {
                        val reaction = it.documents[i].toObject(FilmReaction::class.java)
                        reaction?.let { reaction ->
                            when (reaction.type_reaction) {
                                1 -> {
                                    totalLike += 1

                                    if (reaction.idUser == activity.idUser) {
                                        myReaction = 1
                                        documentIdLikeDislike = it.documents[i].id
                                    }
                                }

                                2 -> {
                                    totalDislike += 1

                                    if (reaction.idUser == activity.idUser) {
                                        myReaction = 2
                                        documentIdLikeDislike = it.documents[i].id
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                    setupUILikeDislike()
                }
            }
    }

    private fun setupUILikeDislike() = with(binding) {
        tvLikeNumber.text = if (totalLike != 0) {
            totalLike.toString()
        } else {
            getString(R.string.like)
        }

        tvDislikeNumber.text = if (totalDislike != 0) {
            totalDislike.toString()
        } else {
            getString(R.string.dislike)
        }

        when (myReaction) {
            1 -> ivLike.setImageResource(R.drawable.ic_liked_film)
            2 -> ivDislike.setImageResource(R.drawable.ic_disliked)
            0 -> {
                ivLike.setImageResource(R.drawable.ic_like_film)
                ivDislike.setImageResource(R.drawable.ic_dislike)
            }
        }
    }

    private fun setupLikeDislike(typeReaction: Int) {
        when (myReaction) {
            /* Xoá bỏ like khi đã like */
            1 -> {
                documentReferenceLikeOrDislike.collection(filmMainHome.id.toString())
                    .document(documentIdLikeDislike)
                    .delete()
                    .addOnCompleteListener {
                        totalLike -= 1
                        myReaction = 0
                        setupUILikeDislike()
                    }
            }

            /* Xoá bỏ dislike khi đã dislike */
            2 -> {
                documentReferenceLikeOrDislike.collection(filmMainHome.id.toString())
                    .document(documentIdLikeDislike)
                    .delete()
                    .addOnCompleteListener {
                        totalDislike -= 1
                        myReaction = 0
                        setupUILikeDislike()
                    }
            }

            /* Trong trường hợp chưa like, dislike thì tạo like, dislike */
            else -> {
                if (typeReaction == 1) {
                    val reaction = FilmReaction(activity.idUser, 1)
                    documentReferenceLikeOrDislike
                        .collection(filmMainHome.id.toString())
                        .add(reaction)
                        .addOnSuccessListener {
                            totalLike += 1
                            myReaction = 1
                            documentIdLikeDislike = it.id
                            setupUILikeDislike()
                        }
                } else {
                    val reaction = FilmReaction(activity.idUser, 2)
                    documentReferenceLikeOrDislike
                        .collection(filmMainHome.id.toString())
                        .add(reaction)
                        .addOnSuccessListener {
                            totalDislike += 1
                            myReaction = 2
                            documentIdLikeDislike = it.id
                            setupUILikeDislike()
                        }
                }
            }
        }
    }

    private fun callApiGetByCategoryListMovie(categoryId: Int, page: Int) {
        ApiService.apiService.getFilmByCategory(Constant.Api.WS_TOKEN, categoryId, page, 15)
            .enqueue(object : Callback<FilmArrayResponse> {
                override fun onResponse(
                    call: Call<FilmArrayResponse>,
                    response: Response<FilmArrayResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            it.data?.let { listFilmRelative ->
                                filmRelativeAdapter.updateData(listFilmRelative as ArrayList)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<FilmArrayResponse?>, t: Throwable) {
                    Log.e(this::class.java.simpleName, t.message.toString())
                }
            })
    }

    private fun loadFavorite() {
        documentReferenceFilmFavorite
            .collection(activity.idUser)
            .whereEqualTo("idFilm", filmMainHome.id)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (querySnapshot.size() > 0) {
                    val reaction = querySnapshot.documents[0].toObject(FilmReaction::class.java)
                    documentIdFilmFavorite = querySnapshot.documents[0].id
                    reaction?.let {
                        statusFavorite = 1
                        binding.apply {
                            ivAddFavorite.setImageResource(R.drawable.ic_added_favorite)
                            tvFavorite.setTextColor(Color.parseColor("#2A48E8"))
                        }
                    }
                } else {
                    statusFavorite = 0
                    binding.apply {
                        ivAddFavorite.setImageResource(R.drawable.ic_add_favorite)
                        tvFavorite.setTextColor(Color.parseColor("#313030"))
                    }
                }
            }
    }

    private fun updateFavorite() {
        if (statusFavorite == 0) {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val reaction = FilmReaction(
                filmMainHome.id,
                filmMainHome.avatar,
                filmMainHome.name,
                format.format(Date())
            )
            documentReferenceFilmFavorite.collection(activity.idUser)
                .add(reaction)
                .addOnCompleteListener {
                    statusFavorite = 1
                    documentIdFilmFavorite = it.result.id
                    binding.apply {
                        ivAddFavorite.setImageResource(R.drawable.ic_added_favorite)
                        tvFavorite.setTextColor(Color.parseColor("#2A48E8"))
                    }
                    Toast.makeText(
                        requireActivity(),
                        activity.getString(R.string.add_film_favorite),
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            documentReferenceFilmFavorite.collection(activity.idUser)
                .document(documentIdFilmFavorite)
                .delete()
                .addOnCompleteListener {
                    statusFavorite = 0
                    binding.apply {
                        ivAddFavorite.setImageResource(R.drawable.ic_add_favorite)
                        tvFavorite.setTextColor(Color.parseColor("#313030"))
                    }
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.remove_film_favorite),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun onClickSubFilm(subFilm: SubFilm) {
        subFilmArrayList.forEach {
            it.setWatching(it.id == subFilm.id)
        }
        mEpisodeAdapter.setData(subFilmArrayList)
        activity.playFilm(subFilm)
    }

    override fun onClickOpenFilmRelate(film: FilmMainHome) {
        viewModel.getFilmDetail(Constant.Api.WS_TOKEN, film.id).observe(requireActivity()) {
            it?.let {
                val subFilm = SubFilm().apply {
                    link = it.data.link
                }
                activity.playFilm(subFilm)
                filmMainHome = it.data
                showInfo()
            }
        }
    }
}