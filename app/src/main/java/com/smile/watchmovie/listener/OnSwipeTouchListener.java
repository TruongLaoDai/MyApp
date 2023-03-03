package com.smile.watchmovie.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context ctx) {
        this.gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public final class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            onSingLeTouch();
            return super.onSingleTapConfirmed(e);
        }
    }

    public void onSingLeTouch() {

    }
}
