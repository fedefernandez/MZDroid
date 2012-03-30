package com.projectsexception.mzdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.util.FieldUtil;

public class PlayersView extends LinearLayout {
    
    private int fieldH;
    private int fieldW;
    private int centralSize;
    private int sidesSize;
    private boolean vertical;

    public PlayersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayersView(Context context) {
        super(context);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (parentWidth > parentHeight) {
            // Apaisado
            vertical = false;
        } else {
            // Vertical
            vertical = true;
        }
        
        int[] dims = FieldUtil.calculateFieldDimmensions(parentWidth, parentHeight);
        fieldW = dims[0];
        fieldH = dims[1];
        
        if (vertical) {
            setOrientation(LinearLayout.VERTICAL);
            setMeasuredDimension(fieldW, fieldH);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, 
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            params.topMargin = 10;
            setLayoutParams(params);
        } else {
            setOrientation(LinearLayout.HORIZONTAL);
            setMeasuredDimension(fieldH, fieldW);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, 
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.LEFT | Gravity.CENTER_VERTICAL);
            params.leftMargin = 10;
            setLayoutParams(params);
        }
        
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centralSize = FieldUtil.calculateCenterDimension(fieldW, fieldH);
        sidesSize = (fieldH - centralSize) / 2;
        View v = findViewById(R.id.players_forwards);
//        v.setBackgroundColor(Color.argb(100, 255, 0, 0));
        if (vertical) {
            v.layout(0, 0, fieldW, sidesSize);
        } else {
            v.layout(0, 0, sidesSize, fieldW);
        }
        v = findViewById(R.id.players_midfields);
//        v.setBackgroundColor(Color.argb(100, 0, 255, 0));
        if (vertical) {
            v.layout(0, sidesSize, fieldW, sidesSize + centralSize);
        } else {
            v.layout(sidesSize, 0, sidesSize + centralSize, fieldW);
        }
        v = findViewById(R.id.players_defenses);
//        v.setBackgroundColor(Color.argb(100, 0, 0, 255));
        if (vertical) {
            v.layout(0, sidesSize + centralSize, fieldW, fieldH);
        } else {
            v.layout(sidesSize + centralSize, 0, fieldH, fieldW);
        }
    }

    public int getFieldH() {
        return fieldH;
    }

    public int getFieldW() {
        return fieldW;
    }

    public int getCentralSize() {
        return centralSize;
    }

    public int getSidesSize() {
        return sidesSize;
    }

    public boolean isVertical() {
        return vertical;
    }

}
