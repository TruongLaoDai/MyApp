package com.smile.watchmovie.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.smile.watchmovie.fragment.MovieFragment;
import com.smile.watchmovie.fragment.VideoFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private int numPage;

    public HomeViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        numPage = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VideoFragment();
            case 1:
                return new MovieFragment();
        }
        return new VideoFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Video";
            case 1:
                return "Phim";
        }
        return "Video";
    }
}