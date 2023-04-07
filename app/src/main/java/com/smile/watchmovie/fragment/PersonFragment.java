package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.activity.HistoryWatchFilmActivity;
import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.HistoryWatchFilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentPersonBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonFragment extends Fragment {

    private FragmentPersonBinding binding;
    private MainActivity mMainActivity;
    private CollectionReference collectionReferenceFilmFavorite;
    private String idUser;
    private CollectionReference collectionReferenceHistory;
    private List<FilmMainHome> mHistoryList;
    private List<Long> timeList;
    private List<Long> durationList;
    private HistoryWatchFilmAdapter historyWatchFilmAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        mHistoryList = new ArrayList<>();
        timeList = new ArrayList<>();
        durationList = new ArrayList<>();

        SharedPreferences sharedPreferences = mMainActivity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");
        String nameUser = sharedPreferences.getString("nameUser", "");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("history_watch_film_" + idUser);

        binding.tvNameAccount.setText(nameUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);

        binding.rcvHistory.setLayoutManager(linearLayoutManager);
        historyWatchFilmAdapter = new HistoryWatchFilmAdapter(mMainActivity);
        historyWatchFilmAdapter.setData(mHistoryList);
        historyWatchFilmAdapter.setTimeList(timeList);
        historyWatchFilmAdapter.setDurationList(durationList);
        binding.rcvHistory.setAdapter(historyWatchFilmAdapter);
        onClickItem();
        getHistoryWatchFilm();

        return binding.getRoot();
    }

    private void onClickItem() {
        binding.tvHistory.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, HistoryWatchFilmActivity.class);
            mMainActivity.startActivity(intent);
        });

        binding.tvDownload.setOnClickListener(v ->
                Toast.makeText(mMainActivity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.tvFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, HistoryWatchFilmActivity.class);
            mMainActivity.startActivity(intent);
        });

         binding.tvSetting.setOnClickListener(v -> {
             Intent intent = new Intent(mMainActivity, HistoryWatchFilmActivity.class);
             mMainActivity.startActivity(intent);
         });

        binding.tvFeedback.setOnClickListener(v ->
                Toast.makeText(mMainActivity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.tvHelp.setOnClickListener(v ->
                Toast.makeText(mMainActivity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void clickDeleteHistory(FilmMainHome movieMainHome) {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView tv_title = dialog.findViewById(R.id.tv_at_time);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        tv_title.setText("Bạn có muốn xóa film " + movieMainHome.getName() + " khỏi lịch sử xem phim của bạn?");


        btn_yes.setOnClickListener(v -> {
            collectionReferenceHistory.whereEqualTo("idFilm", movieMainHome.getId() + "")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            collectionReferenceHistory.document(documentId)
                                    .delete();
                        }
                    });
            mHistoryList.remove(movieMainHome);
//            if(mHistoryList.size() == 0){
//                binding.tvSizeHistory.setVisibility(View.VISIBLE);
//            }
            historyWatchFilmAdapter.notifyDataSetChanged();
            dialog.dismiss();
            Toast.makeText(mMainActivity, "Bạn đã xóa film thành công", Toast.LENGTH_LONG).show();
        });


        btn_no.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void getHistoryWatchFilm() {
        collectionReferenceHistory.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    String time = Objects.requireNonNull(doc.get("time")).toString();
                    String duration = Objects.requireNonNull(doc.get("duration")).toString();
                    timeList.add(Long.parseLong(time));
                    durationList.add(Long.parseLong(duration));
                    callApiGetHistoryWatchFilm(Integer.parseInt(idFilm1));
                }
                binding.progressLoadPersonHome.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void callApiGetHistoryWatchFilm(int idFilm) {
        ApiService.apiService.getFilmDetail(getString(R.string.wsToken), idFilm).enqueue(new Callback<FilmDetailResponse>() {
            @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                FilmDetailResponse cinema = response.body();
                if (cinema != null) {
                    mHistoryList.add(cinema.getData());
                    historyWatchFilmAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(mMainActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }
}