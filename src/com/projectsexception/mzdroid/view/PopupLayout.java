package com.projectsexception.mzdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.projectsexception.mzdroid.util.CustomLog;

public class PopupLayout extends FrameLayout {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    private GestureDetector gestureDetector;

    public PopupLayout(Context context) {
        super(context);
        buildGestureDetector();
        buildAnimation();
    }

    public PopupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildGestureDetector();
        buildAnimation();
    }

    public PopupLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        buildGestureDetector();
        buildAnimation();
    }
    
    public void closePopup() {
        final ViewGroup view = (ViewGroup) getParent();
        if (view != null) {
            Animation animation = closeAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
    
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
    
                @Override
                public void onAnimationEnd(Animation animation) {
                    view.removeView(PopupLayout.this);
                }
            });
            startAnimation(animation);
        }
    }
    
    private void buildAnimation() {
        LayoutAnimationController controller = new LayoutAnimationController(openAnimation());
        setLayoutAnimation(controller);
    }
    
    private void buildGestureDetector() {
        gestureDetector = new GestureDetector(getContext(), new MyGestureDetector());
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CustomLog.debug("PopupLayout", "onTouchEvent");
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return true;
            }
        });
    }

    private static Animation openAnimation() {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        return animation;
    }

    private static Animation closeAnimation() {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);
        animation.setDuration(500);
        return animation;
    }
    
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            CustomLog.debug("PopupLayout", "From " + e1.getY() + " to " + e2.getY());
            CustomLog.debug("PopupLayout", "Velocity " + velocityY);
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {                    
                closePopup();
            }
            return false;
        }
        
    }

}
