package com.smile.watchmovie.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.smile.watchmovie.fragment.DescribeChannelFragment;
import com.smile.watchmovie.fragment.VideoChannelFragment;

public class ViewPagerChannelAdapter extends FragmentStatePagerAdapter {

    private int idChannel;


    public ViewPagerChannelAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setIdChannel(int idChannel){
        this.idChannel=idChannel;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_channel", this.idChannel);
        VideoChannelFragment fragVideoChannel = new VideoChannelFragment();
        fragVideoChannel.setArguments(bundle);
        DescribeChannelFragment fragDescribeChannel=new DescribeChannelFragment();
        fragDescribeChannel.setArguments(bundle);

        switch (position){
            case 0:
                return fragVideoChannel;
            case 1:
                return fragDescribeChannel;
        }
        return fragVideoChannel;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        switch (position){
            case 0:
                title = "Videos";
                break;
            case 1:
                title = "Giới thiệu";
                break;
        }
        return title;
    }
}
