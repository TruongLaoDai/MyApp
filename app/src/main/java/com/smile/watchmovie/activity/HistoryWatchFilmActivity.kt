package com.smile.watchmovie.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.smile.watchmovie.adapter.FavoriteFilmAdapter
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding
import com.smile.watchmovie.utils.Constant

class HistoryWatchFilmActivity : AppCompatActivity(), FavoriteFilmAdapter.OnListener {
    private lateinit var binding: ActivityHistoryWatchFilmBinding
    private lateinit var idUser: String
    private lateinit var collectionReferenceHistory: CollectionReference
    private lateinit var adapterHistoryWatched: FavoriteFilmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryWatchFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeData()
        handleEventClick()
        getHistoryWatchedInDB()
    }

    private fun getHistoryWatchedInDB() {
        collectionReferenceHistory.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED)
            .collection(idUser)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.progressLoadHistoryHome.visibility = View.GONE
                    if (!snapshot.isEmpty) {
                        /* Để đây, có thời gian thì làm tiếp */
                        adapterHistoryWatched
                    } else {
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

        adapterHistoryWatched = FavoriteFilmAdapter(this, this)
        binding.rcvHistory.apply {
            setHasFixedSize(true)
            adapter = adapterHistoryWatched
        }

        val firebaseFirestore = FirebaseFirestore.getInstance()
        collectionReferenceHistory =
            firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE)
    }

    override fun openFilm(idFilm: Int) {

    }

    override fun deleteToFavorite(documentId: String) {

    }
}