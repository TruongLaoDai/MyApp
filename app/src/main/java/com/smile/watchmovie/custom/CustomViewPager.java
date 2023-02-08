package com.smile.watchmovie.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean isPageScrollEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isPageScrollEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(this.isPageScrollEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(this.isPageScrollEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setPageScrollEnabled(boolean isPageScrollEnabled) {
        this.isPageScrollEnabled = isPageScrollEnabled;
    }
}
