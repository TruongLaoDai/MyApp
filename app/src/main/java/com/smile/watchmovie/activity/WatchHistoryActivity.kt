package com.smile.watchmovie.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.smile.watchmovie.adapter.WatchHistoryAdapter
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding
import com.smile.watchmovie.dialog.ConfirmDeleteDialog
import com.smile.watchmovie.listener.IClickItemFilmListener
import com.smile.watchmovie.model.HistoryWatchFilm
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.FavoriteFilmActivityViewModel

class WatchHistoryActivity : AppCompatActivity(),
    WatchHistoryAdapter.OnListener, IClickItemFilmListener {
    private lateinit var binding: ActivityHistoryWatchFilmBinding
    private lateinit var idUser: String
    private lateinit var collectionReference: CollectionReference
    private lateinit var adapterHistoryWatched: WatchHistoryAdapter
    private lateinit var viewModel: FavoriteFilmActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryWatchFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        handleEventClick()
        getHistoryWatchedInDB()
    }

    private fun getHistoryWatchedInDB() {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED)
            .collection(idUser)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.progressLoadHistoryHome.visibility = View.GONE
                    if (!snapshot.isEmpty) {
                        val arrayList = snapshot.toObjects(HistoryWatchFilm::class.java)
                        for (i in 0 until snapshot.size()) {
                            arrayList[i].documentID = snapshot.documents[i].id
                        }
                        adapterHistoryWatched.updateData(arrayList)
                    } else {
                        adapterHistoryWatched.updateData(ArrayList())
                        binding.tvContent.visibility = View.VISIBLE
                    }
                } else {
                    binding.apply {
                        progressLoadHistoryHome.visibility = View.GONE
                        tvContent.visibility = View.VISIBLE
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

        adapterHistoryWatched = WatchHistoryAdapter(this, this)
        binding.rcvHistory.apply {
            setHasFixedSize(true)
            adapter = adapterHistoryWatched
        }

        val firebaseFirestore = FirebaseFirestore.getInstance()
        collectionReference =
            firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE)

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

    override fun deleteWatchHistory(documentId: String, position: Int) {
        val dialog = ConfirmDeleteDialog(documentId, position, this)
        dialog.show(supportFragmentManager, null)
    }

    override fun onClickItemFilm(documentId: String, position: Int) {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED)
            .collection(idUser)
            .document(documentId)
            .delete()
            .addOnCompleteListener {
                adapterHistoryWatched.removeItem(position)
            }
    }
}