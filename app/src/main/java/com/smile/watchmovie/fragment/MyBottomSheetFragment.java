package com.smile.watchmovie.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {

    private FilmMainHome filmDetail;

    public MyBottomSheetFragment(FilmMainHome filmDetail) {
        this.filmDetail = filmDetail;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.frangment_detail_film, null);
        bottomSheetDialog.setContentView(view);

        TextView tvNameFilm, tvCategory, tvCreated, tvDirectors, tvDescription;
        ImageView ivLogoFilm, iv_close;

        tvNameFilm = view.findViewById(R.id.tv_name_film);
        tvCategory = view.findViewById(R.id.tv_category);
        tvCreated = view.findViewById(R.id.tv_created);
        tvDirectors = view.findViewById(R.id.tv_directors);
        tvDescription = view.findViewById(R.id.tv_description);
        ivLogoFilm = view.findViewById(R.id.img_logo_film);
        iv_close = view.findViewById(R.id.iv_close);

        tvNameFilm.setText(filmDetail.getName());
        tvCreated.setText(filmDetail.getCreated());
        tvDescription.setText(filmDetail.getDescription());
        Glide.with(view.getContext()).load(filmDetail.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(ivLogoFilm);
        iv_close.setOnClickListener(v -> dismiss());
        //tvCategory
        return bottomSheetDialog;
    }
}
