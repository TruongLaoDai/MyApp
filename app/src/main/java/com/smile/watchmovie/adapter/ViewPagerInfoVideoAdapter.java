package com.smile.watchmovie.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.smile.watchmovie.fragment.CommentFragment;
import com.smile.watchmovie.fragment.DescribeChannelFragment;
import com.smile.watchmovie.fragment.IntroVideoFragment;
import com.smile.watchmovie.fragment.VideoChannelFragment;

public class ViewPagerInfoVideoAdapter extends FragmentStatePagerAdapter {

    private int idVideo=0;

    public ViewPagerInfoVideoAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setIdVideo(int idVideo){
        this.idVideo=idVideo;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_video", this.idVideo);
        IntroVideoFragment introVideoFragment = new IntroVideoFragment();
        introVideoFragment.setArguments(bundle);
        CommentFragment commentFragment=new CommentFragment();
        commentFragment.setArguments(bundle);
        switch (position){
            case 0:
                return introVideoFragment;
            case 1:
                return commentFragment;
        }
        return introVideoFragment;
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
                title = "Giới thiệu";
                break;
            case 1:
                title = "Bình luận";
                break;
        }
        return title;
    }
}
