package com.smile.watchmovie.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.smile.watchmovie.databinding.DeleteFragmentBinding;
import com.smile.watchmovie.listener.IClickItemFilmListener;

public class ConfirmDeleteDialog extends BottomSheetDialogFragment {
    private String documentId;
    private IClickItemFilmListener listener;
    private DeleteFragmentBinding binding;

    public ConfirmDeleteDialog() {
    }

    public ConfirmDeleteDialog(String documentId, IClickItemFilmListener listener) {
        this.documentId = documentId;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DeleteFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDelete.setOnClickListener(v -> {
            listener.onClickItemFilm(documentId);
            dismiss();
        });

        binding.btnCancel.setOnClickListener(v -> dismiss());
    }
}
