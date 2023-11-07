package com.smile.watchmovie.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smile.watchmovie.fragment.CommentFragment;
import com.smile.watchmovie.fragment.InfoFilmFragment;

public class WatchFilmViewPagerAdapter extends FragmentStateAdapter {

    public WatchFilmViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new CommentFragment();
        }
        return new InfoFilmFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
