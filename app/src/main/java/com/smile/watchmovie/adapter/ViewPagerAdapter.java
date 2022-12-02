package com.smile.watchmovie.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.smile.watchmovie.fragment.HomeFragment;
import com.smile.watchmovie.fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior){
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new SearchFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
