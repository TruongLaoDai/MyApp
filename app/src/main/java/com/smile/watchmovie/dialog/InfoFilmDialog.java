package com.smile.watchmovie.dialog;

import android.annotation.SuppressLint;
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
import com.smile.watchmovie.model.FilmMainHome;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InfoFilmDialog extends BottomSheetDialogFragment {
    private final FilmMainHome filmDetail;

    public InfoFilmDialog(FilmMainHome filmDetail) {
        this.filmDetail = filmDetail;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.frangment_detail_film, null);
        bottomSheetDialog.setContentView(view);

        TextView tvNameFilm, tvCreated, tvDescription, rate;
        ImageView ivLogoFilm;

        tvNameFilm = view.findViewById(R.id.tv_name_film);
        tvCreated = view.findViewById(R.id.tv_created);
        tvDescription = view.findViewById(R.id.tv_description);
        ivLogoFilm = view.findViewById(R.id.img_logo_film);
        rate = view.findViewById(R.id.rate);

        tvNameFilm.setText(filmDetail.getName());
        tvCreated.setText("Ngày ra mắt: " + dateCreated(filmDetail.getCreated()));
        rate.setText("Điểm đánh giá: " + filmDetail.getImdbPoint() + "/10");
        tvDescription.setText(filmDetail.getDescription());
        Glide.with(view.getContext()).load(filmDetail.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(ivLogoFilm);

        return bottomSheetDialog;
    }

    private String dateCreated(String date) {
        String[] data = date.split("T");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = format.parse(data[0]);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            assert date1 != null;
            return format1.format(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
