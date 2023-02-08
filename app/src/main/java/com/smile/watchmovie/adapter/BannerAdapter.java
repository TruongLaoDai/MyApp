package com.smile.watchmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.R;
import com.smile.watchmovie.model.Banner;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private final Context mContext;
    private List<Banner> mListBanner;

    public BannerAdapter(Context context){
        mContext = context;
    }

    public void setData(List<Banner> mListPhoto) {
        this.mListBanner = mListPhoto;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo, container, false);
        Banner photo = mListBanner.get(position);
        ImageView iv_photo = view.findViewById(R.id.iv_photo);
        if(photo != null){
            Glide.with(mContext).load(photo.getUrl())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .placeholder(R.drawable.ic_baseline_image_gray)
                    .into(iv_photo);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(mListBanner != null){
            return mListBanner.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
