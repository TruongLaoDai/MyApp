package com.smile.watchmovie.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smile.watchmovie.fragment.IntroduceFilmFragment;

public class WatchFilmViewPagerAdapter extends FragmentStatePagerAdapter {
    public WatchFilmViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new IntroduceFilmFragment();
            default:
                return new IntroduceFilmFragment();
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Giới thiệu";
            default:
                return "null";
        }
    }
}
