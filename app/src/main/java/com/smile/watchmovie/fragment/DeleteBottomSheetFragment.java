package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.smile.watchmovie.R;
import com.smile.watchmovie.listener.IClickItemDeleteHistoryListener;
import com.smile.watchmovie.listener.IClickItemUnFavoriteListener;
import com.smile.watchmovie.model.HistoryWatchFilm;
import com.smile.watchmovie.model.FilmReaction;

public class DeleteBottomSheetFragment extends BottomSheetDialogFragment {

    private HistoryWatchFilm historyWatchFilm;
    private FilmReaction favoriteFilm;
    private IClickItemDeleteHistoryListener deleteHistoryListener;
    private IClickItemUnFavoriteListener unFavoriteListener;

    public DeleteBottomSheetFragment() {
    }

    public void setHistoryWatchFilm(HistoryWatchFilm historyWatchFilm) {
        this.historyWatchFilm = historyWatchFilm;
    }

    public void setFavoriteFilm(FilmReaction favoriteFilm) {
        this.favoriteFilm = favoriteFilm;
    }

    public void setDeleteHistoryListener(IClickItemDeleteHistoryListener deleteHistoryListener){
        this.deleteHistoryListener = deleteHistoryListener;
    }

    public void setUnFavoriteFilmListener(IClickItemUnFavoriteListener unFavoriteListener) {
        this.unFavoriteListener = unFavoriteListener;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_fragment, null);
        bottomSheetDialog.setContentView(view);

        RelativeLayout lout_delete;
        Button btn_cancel;

        lout_delete = view.findViewById(R.id.lout_delete);
        btn_cancel = view.findViewById(R.id.btn_delete);

        lout_delete.setOnClickListener(v ->{
            if(historyWatchFilm != null) {
                deleteHistoryListener.onClickDeleteHistoryListener(historyWatchFilm);
            } else {
                unFavoriteListener.onClickUnFavoriteListener(favoriteFilm);
            }
            dismiss();
        });
        btn_cancel.setOnClickListener(v -> dismiss());
        return bottomSheetDialog;
    }
}
