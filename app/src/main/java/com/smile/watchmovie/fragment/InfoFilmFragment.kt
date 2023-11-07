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
import com.smile.watchmovie.activity.WatchFilmActivity
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
    private lateinit var activity: WatchFilmActivity
    private lateinit var filmRelativeAdapter: FilmRelativeAdapter
    private lateinit var viewModel: InfoFilmFragmentViewModel
    private var idUser: String = ""
    private lateinit var filmMainHome: FilmMainHome
    private lateinit var mEpisodeAdapter: EpisodeAdapter
    private lateinit var documentReferenceFilmFavorite: DocumentReference
    private lateinit var documentReferenceFilmLike: DocumentReference
    private lateinit var documentReferenceFilmDislike: DocumentReference
    private var changeImageFavoriteFilm = 0
    private var changeImageDislikeFilm = 0
    private var changeImageLikeFilm = 0
    private var currentLike = 0
    private var currentDislike = 0
    private var statusLike = 0
    private var statusDislike = 0
    private var statusFavorite = 0
    private lateinit var mediaLike: FilmReaction
    private lateinit var mediaDislike: FilmReaction
    private lateinit var mediaFavorite: FilmReaction
    private lateinit var subFilmArrayList: List<SubFilm>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false)

        activity = requireActivity() as WatchFilmActivity
        initializeData()

        if (activity.idUser != "") {
            setUpFireBase()
        }
        handleEventClick()

        return binding.root
    }

    private fun initialFilmRelate() {
        callApiGetByCategoryListMovie(filmMainHome.categoryId, (0..10).random())
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
        }

        /* Nhấn like */binding.loutLike.setOnClickListener { v: View? ->
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    "Bạn cần đăng nhập tài khoản để thực hiện tính năng này",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setUpViewLikeFilm()
            }
        }

        /* Nhấn dislike */binding.loutDislike.setOnClickListener { v: View? ->
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    "Bạn cần đăng nhập tài khoản để thực hiện tính năng này",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setUpViewDislikeFilm()
            }
        }

        /* Nhấn yêu thích */binding.loutFavorite.setOnClickListener { v: View? ->
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    "Bạn cần đăng nhập tài khoản để thực hiện tính năng này",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setUpViewFavoriteFilm()
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
        idUser = activity.idUser
        val firebaseFirestore = FirebaseFirestore.getInstance()
        documentReferenceFilmFavorite = firebaseFirestore.document("WatchFilm/tblfilmfavorite")
        documentReferenceFilmLike = firebaseFirestore.document("WatchFilm/tblfilmlike")
        documentReferenceFilmDislike = firebaseFirestore.document("WatchFilm/tblfilmdislike")
        loadFavorite()
        loadLike()
        loadDislike()
    }

    private fun setUpViewFavoriteFilm() {
        if (changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_added_favorite
            binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"))
            Toast.makeText(
                activity,
                activity.getString(R.string.add_film_favorite),
                Toast.LENGTH_LONG
            ).show()
        } else if (changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_add_favorite
            binding.tvFavorite.setTextColor(Color.parseColor("#777776"))
            Toast.makeText(
                activity,
                activity.getString(R.string.remove_film_favorite),
                Toast.LENGTH_LONG
            ).show()
        }
        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm)
        updateFavorite()
    }

    private fun setUpViewLikeFilm() {
        if (changeImageLikeFilm == R.drawable.ic_like_film) {
            if (changeImageDislikeFilm == R.drawable.ic_disliked) {
                currentDislike -= 1
                if (currentDislike < 0) {
                    currentDislike = 0
                }
                binding.tvDislikeNumber.text = activity.getString(R.string.dislike)
                changeImageDislikeFilm = R.drawable.ic_dislike
                binding.ivDislike.setImageResource(changeImageDislikeFilm)
                binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"))
            }
            changeImageLikeFilm = R.drawable.ic_liked_film
            binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"))
            currentLike += 1
            binding.tvLikeNumber.text = activity.getString(R.string.number, currentLike)
        } else if (changeImageLikeFilm == R.drawable.ic_liked_film) {
            changeImageLikeFilm = R.drawable.ic_like_film
            binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"))
            currentLike -= 1
            if (currentLike < 0) {
                currentLike = 0
            }
            binding.tvLikeNumber.text = activity.getString(R.string.like)
        }
        binding.ivLike.setImageResource(changeImageLikeFilm)
        updateFilmLike()
        updateFilmDisLike()
    }

    private fun setUpViewDislikeFilm() {
        if (changeImageDislikeFilm == R.drawable.ic_dislike) {
            if (changeImageLikeFilm == R.drawable.ic_liked_film) {
                currentLike -= 1
                if (currentLike < 0) {
                    currentLike = 0
                }
                binding.tvLikeNumber.text = activity.getString(R.string.like)
                changeImageLikeFilm = R.drawable.ic_like_film
                binding.ivLike.setImageResource(changeImageLikeFilm)
                binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"))
            }
            changeImageDislikeFilm = R.drawable.ic_disliked
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"))
            currentDislike += 1
            binding.tvDislikeNumber.text = activity.getString(R.string.number, currentDislike)
        } else if (changeImageDislikeFilm == R.drawable.ic_disliked) {
            changeImageDislikeFilm = R.drawable.ic_dislike
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"))
            currentDislike -= 1
            if (currentDislike < 0) {
                currentDislike = 0
            }
            binding.tvDislikeNumber.text = activity.getString(R.string.dislike)
        }
        binding.ivDislike.setImageResource(changeImageDislikeFilm)
        updateFilmDisLike()
        updateFilmLike()
    }

    private fun callApiGetByCategoryListMovie(categoryId: Int, page: Int) {
        ApiService.apiService.getFilmByCategory(Constant.Api.WS_TOKEN, categoryId, page, 15)
            .enqueue(object : Callback<FilmArrayResponse> {
                override fun onResponse(
                    call: Call<FilmArrayResponse>,
                    response: Response<FilmArrayResponse>
                ) {
                    if (response.isSuccessful) {
                        filmRelativeAdapter.updateData(response.body()?.data as ArrayList)
                    }
                }

                override fun onFailure(call: Call<FilmArrayResponse?>, t: Throwable) {
                    Log.e(this::class.java.simpleName, t.message.toString())
                }
            })
    }

    fun loadFavorite() {
        documentReferenceFilmFavorite
            .collection(idUser)
            .whereEqualTo("idFilm", filmMainHome.id)
            .get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                if (queryDocumentSnapshots.size() > 0) {
                    mediaFavorite = queryDocumentSnapshots.documents[0].toObject(
                        FilmReaction::class.java
                    )!!
                    if (mediaFavorite != null) {
                        mediaFavorite.documentId = queryDocumentSnapshots.documents[0].id
                        if (mediaFavorite.type_reaction == 1) {
                            statusFavorite = 2
                            changeImageFavoriteFilm = R.drawable.ic_added_favorite
                            binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm)
                            binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"))
                        } else {
                            changeImageFavoriteFilm = R.drawable.ic_add_favorite
                            statusFavorite = 1
                        }
                    } else {
                        statusFavorite = 0
                    }
                } else {
                    changeImageFavoriteFilm = R.drawable.ic_add_favorite
                    statusFavorite = 0
                }
            }
    }

    fun loadLike() {
        documentReferenceFilmLike
            .collection(filmMainHome.id.toString() + "")
            .whereEqualTo("idUser", idUser)
            .get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                if (queryDocumentSnapshots.size() > 0) {
                    mediaLike = queryDocumentSnapshots.documents[0].toObject(
                        FilmReaction::class.java
                    )!!
                    if (mediaLike != null) {
                        mediaLike.documentId = queryDocumentSnapshots.documents[0].id
                        if (mediaLike.type_reaction == 1) {
                            statusLike = 2
                            changeImageDislikeFilm = R.drawable.ic_dislike
                            changeImageLikeFilm = R.drawable.ic_liked_film
                            binding.ivLike.setImageResource(changeImageLikeFilm)
                            binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"))
                        } else {
                            changeImageLikeFilm = R.drawable.ic_like_film
                            statusLike = 1
                        }
                    } else {
                        statusLike = 0
                    }
                } else {
                    changeImageLikeFilm = R.drawable.ic_like_film
                    statusLike = 0
                }
            }
    }

    fun loadDislike() {
        documentReferenceFilmDislike
            .collection(filmMainHome.id.toString() + "")
            .whereEqualTo("idUser", idUser)
            .get().addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                if (queryDocumentSnapshots.size() > 0) {
                    mediaDislike = queryDocumentSnapshots.documents[0].toObject(
                        FilmReaction::class.java
                    )!!
                    if (mediaDislike != null) {
                        mediaDislike.documentId = queryDocumentSnapshots.documents[0].id
                        if (mediaDislike.type_reaction == 1) {
                            statusDislike = 2
                            changeImageDislikeFilm = R.drawable.ic_disliked
                            changeImageLikeFilm = R.drawable.ic_like_film
                            binding.ivDislike.setImageResource(changeImageDislikeFilm)
                            binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"))
                        } else {
                            changeImageDislikeFilm = R.drawable.ic_dislike
                            statusDislike = 1
                        }
                    } else {
                        statusDislike = 0
                    }
                } else {
                    changeImageDislikeFilm = R.drawable.ic_dislike
                    statusDislike = 0
                }
            }
    }

    private fun updateFavorite() {
        if (statusFavorite == 1 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            documentReferenceFilmFavorite
                .collection(idUser)
                .document(mediaFavorite.documentId)
                .update("type_reaction", 1)
        } else if (statusFavorite == 2 && changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            documentReferenceFilmFavorite
                .collection(idUser)
                .document(mediaFavorite.documentId)
                .delete()
        } else if (statusFavorite == 0 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val mediaReaction = FilmReaction(
                filmMainHome.id, filmMainHome.avatar, filmMainHome.name, format.format(
                    Date()
                ), 1
            )
            documentReferenceFilmFavorite.collection(idUser).add(mediaReaction)
        }
    }

    private fun updateFilmLike() {
        if (statusLike == 1 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            documentReferenceFilmLike
                .collection(filmMainHome.id.toString() + "")
                .document(mediaLike.documentId)
                .update("type_reaction", 1)
        } else if (statusLike == 2 && changeImageLikeFilm == R.drawable.ic_like_film) {
            documentReferenceFilmLike
                .collection(filmMainHome.id.toString() + "")
                .document(mediaLike.documentId)
                .delete()
        } else if (statusLike == 0 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val mediaReaction = FilmReaction(idUser, format.format(Date()), 1)
            documentReferenceFilmLike.collection(filmMainHome.id.toString() + "")
                .add(mediaReaction)
        }
    }

    private fun updateFilmDisLike() {
        if (statusDislike == 1 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            documentReferenceFilmDislike
                .collection(filmMainHome.id.toString() + "")
                .document(mediaDislike.documentId)
                .update("type_reaction", 1)
        } else if (statusDislike == 2 && changeImageDislikeFilm == R.drawable.ic_dislike) {
            documentReferenceFilmDislike
                .collection(filmMainHome.id.toString() + "")
                .document(mediaDislike.documentId)
                .delete()
        } else if (statusDislike == 0 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val mediaReaction = FilmReaction(idUser, format.format(Date()), 1)
            documentReferenceFilmDislike.collection(filmMainHome.id.toString() + "")
                .add(mediaReaction)
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