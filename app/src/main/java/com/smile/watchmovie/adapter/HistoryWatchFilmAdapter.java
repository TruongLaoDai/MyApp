package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.DetailFilmActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ItemFilmFavoriteBinding;
import com.smile.watchmovie.databinding.ItemFilmHistoryBinding;
import com.smile.watchmovie.model.MovieMainHome;

import org.json.JSONException;

import java.util.List;

public class HistoryWatchFilmAdapter extends RecyclerView.Adapter<HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder> {

    private List<MovieMainHome> movieMainHomeList;
    private final Context context;
    private String idUser;

    public HistoryWatchFilmAdapter(Context context) {
        this.context = context;
        getIdUser();
    }

    public void setData(List<MovieMainHome> movieMainHomeList) {
        this.movieMainHomeList = movieMainHomeList;
    }

    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct != null){
            this.idUser = acct.getId();
        }
        else if(accessToken != null && !accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        // Application code
                        try {
                            assert object != null;
                            this.idUser = (String) object.get("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    @NonNull
    @Override
    public HistoryWatchFilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder(ItemFilmHistoryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryWatchFilmViewHolder holder, int position) {
        MovieMainHome movieMainHome = movieMainHomeList.get(position);
        if (movieMainHome == null) {
            return;
        }

        Glide.with(context).load(movieMainHome.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.binding.ivImageFilm);
        int episodesTotal = movieMainHome.getEpisodesTotal();
        if (episodesTotal == 0) {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
        } else {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, movieMainHome.getEpisodesTotal()));
        }

        holder.binding.tvStar.setText(context.getString(R.string.film_start, movieMainHome.getStar()));
        holder.binding.tvNameFilm.setText(movieMainHome.getName());
        holder.binding.layoutFilm.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailFilmActivity.class);
            intent.putExtra("id_detail_film", movieMainHome.getId());
            context.startActivity(intent);
        });
        holder.binding.ivDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
                dialog.setCancelable(false);

                Window window = dialog.getWindow();
                if(window == null){
                    return;
                }

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                window.setAttributes(windowAttributes);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = firebaseFirestore.collection("history_watch_film_"+ idUser);
                TextView tv_title = dialog.findViewById(R.id.tv_at_time);
                Button btn_yes = dialog.findViewById(R.id.btn_yes);
                Button btn_no = dialog.findViewById(R.id.btn_no);

                tv_title.setText("Bạn có muốn xóa film " + movieMainHome.getName() + " khỏi lịch sử xem phim của bạn?");


                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        collectionReference.whereEqualTo("idFilm", movieMainHome.getId() +"")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String documentId = documentSnapshot.getId();
                                            collectionReference.document(documentId)
                                                    .delete();
                                        }
                                    }
                                });
                        movieMainHomeList.remove(movieMainHome);
                        notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(context, "Bạn đã xóa film thành công",Toast.LENGTH_LONG).show();
                    }
                });

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class HistoryWatchFilmViewHolder extends RecyclerView.ViewHolder{

        private final ItemFilmHistoryBinding binding;

        public HistoryWatchFilmViewHolder(@NonNull ItemFilmHistoryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
