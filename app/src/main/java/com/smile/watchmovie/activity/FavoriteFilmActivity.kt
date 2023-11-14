package com.smile.watchmovie.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.smile.watchmovie.adapter.FavoriteFilmAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.ActivityFavoriteFilmBinding
import com.smile.watchmovie.dialog.ConfirmDeleteDialog
import com.smile.watchmovie.listener.IClickItemUnFavoriteListener
import com.smile.watchmovie.model.FilmReaction
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.FavoriteFilmActivityViewModel

class FavoriteFilmActivity : AppCompatActivity(), FavoriteFilmAdapter.OnListener,
    IClickItemUnFavoriteListener {
    private lateinit var binding: ActivityFavoriteFilmBinding
    private lateinit var idUser: String
    private lateinit var collectionReference: CollectionReference
    private lateinit var adapter: FavoriteFilmAdapter
    private lateinit var viewModel: FavoriteFilmActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        if (idUser != "") {
            getListFavoriteFilm()
        }
        handleEventClick()
    }

    private fun getListFavoriteFilm() {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_FAVORITE_FILM)
            .collection(idUser)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.progressLoadFavoriteHome.visibility = View.GONE
                    if (!snapshot.isEmpty) {
                        val arrayList = snapshot.toObjects(FilmReaction::class.java)
                        for (i in 0 until snapshot.size()) {
                            arrayList[i].documentId = snapshot.documents[i].id
                        }
                        adapter.updateData(arrayList)
                    } else {
                        binding.apply {
                            tvContent.visibility = View.VISIBLE
                            progressLoadFavoriteHome.visibility = View.GONE
                        }
                    }
                } else {
                    binding.apply {
                        tvContent.visibility = View.VISIBLE
                        progressLoadFavoriteHome.visibility = View.GONE
                    }
                }
            }
    }

    private fun handleEventClick() {
        binding.toolBar.setNavigationOnClickListener { finish() }
    }

    private fun initializeData() {
        val sharedPreferences =
            getSharedPreferences(Constant.NAME_DATABASE_SHARED_PREFERENCES, MODE_PRIVATE)
        idUser = sharedPreferences.getString(Constant.ID_USER, "")!!

        adapter = FavoriteFilmAdapter(this, this)

        binding.apply {
            rcvFavorite.adapter = adapter
            rcvFavorite.setHasFixedSize(true)
        }

        val firebaseFirestore = FirebaseFirestore.getInstance()
        collectionReference = firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceFilm))
        )[FavoriteFilmActivityViewModel::class.java]
    }

    override fun openFilm(idFilm: Int) {
        viewModel.getFilmDetail(Constant.Api.WS_TOKEN, idFilm).observe(this) {
            it?.let {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("film", it.data)
                startActivity(intent)
            }
        }
    }

    override fun deleteToFavorite(documentId: String) {
        val dialog = ConfirmDeleteDialog()
        dialog.apply {
            setFavoriteFilm(documentId)
            setUnFavoriteFilmListener(this@FavoriteFilmActivity)
            show(supportFragmentManager, null)
        }
    }

    override fun onClickUnFavoriteListener(documentId: String) {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_FAVORITE_FILM)
            .collection(idUser)
            .document(documentId)
            .delete()
            .addOnCompleteListener {
                getListFavoriteFilm()
            }
    }
}