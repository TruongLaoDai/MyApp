package com.smile.watchmovie.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.smile.watchmovie.R
import com.smile.watchmovie.activity.BuyPremiumActivity
import com.smile.watchmovie.activity.FavoriteFilmActivity
import com.smile.watchmovie.activity.HistoryWatchFilmActivity
import com.smile.watchmovie.activity.InfoAccountActivity
import com.smile.watchmovie.activity.LoginActivity
import com.smile.watchmovie.activity.PrivateSettingActivity
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.api.RetrofitBuilder
import com.smile.watchmovie.base.ViewModelFactory
import com.smile.watchmovie.databinding.FragmentPersonBinding
import com.smile.watchmovie.eventbus.EventNotifyLogIn
import com.smile.watchmovie.utils.Constant
import com.smile.watchmovie.viewmodel.PersonFragmentViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PersonFragment : Fragment() {
    private lateinit var binding: FragmentPersonBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: PersonFragmentViewModel
    private var nameUser: String? = null
    private var idUser: String? = null
    private var isVip: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /* Khởi tạo dữ liệu */
        binding = FragmentPersonBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constant.NAME_DATABASE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiServiceWeather))
        )[PersonFragmentViewModel::class.java]

        getInfoUserInDB()
        getDataFromAPI()
        setupInfo()
        onClickItem()

        return binding.root
    }

    private fun getDataFromAPI() {
        viewModel.getInfoWeather("Hanoi", "metric", Constant.Api.APP_ID)
            .observe(requireActivity()) {
                it?.let {
                    binding.apply {
                        tvWeather.text =
                            requireActivity().getString(R.string.title_weather, it.main.temp)
                        tvWeatherDetail.text = it.weather[0].main
                    }
                }
            }
    }

    private fun setupInfo() {
        if (isVip != "0") {
            binding.ivVip.visibility = View.VISIBLE
            binding.tvTitlePayDetail.setText(R.string.account_vip)
        } else {
            binding.ivVip.visibility = View.GONE
            binding.tvTitlePayDetail.setText(R.string.buy_title)
        }

        if (nameUser != "") {
            binding.tvNameAccount.text = nameUser
            binding.ivAvtAccount.visibility = View.VISIBLE
            loadAvatarUser()
        } else {
            binding.ivAvtAccount.visibility = View.GONE
            binding.tvNameAccount.text = requireActivity().getString(R.string.login)
        }
    }

    private fun onClickItem() {
        binding.tvHistory.setOnClickListener {
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.not_logged_in_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        HistoryWatchFilmActivity::class.java
                    )
                )
            }
        }

        binding.tvDownload.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                getString(R.string.feature_deploying),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tvFavorite.setOnClickListener {
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.not_logged_in_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        FavoriteFilmActivity::class.java
                    )
                )
            }
        }

        binding.tvSetting.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    PrivateSettingActivity::class.java
                )
            )
        }

        binding.tvHelpFeedback.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                getString(R.string.feature_deploying),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.loutPay.setOnClickListener {
            if (idUser == null || idUser == "") {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.not_logged_in_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requireActivity().startActivity(
                    Intent(requireActivity(), BuyPremiumActivity::class.java)
                )
            }
        }

        binding.loutAccount.setOnClickListener {
            if (idUser == "") {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            } else {
                startActivity(Intent(requireActivity(), InfoAccountActivity::class.java))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(isLogIn: EventNotifyLogIn?) {
        getInfoUserInDB()
        setupInfo()
    }

    private fun getInfoUserInDB() {
        idUser = sharedPreferences.getString(Constant.ID_USER, "")
        nameUser = sharedPreferences.getString(Constant.NAME_USER, "")
        isVip = sharedPreferences.getString(Constant.IS_VIP, "0")
    }

    private fun loadAvatarUser() {
        val signInAccount = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (signInAccount != null) {
            Glide.with(this).load(signInAccount.photoUrl).into(binding.ivAvtAccount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}