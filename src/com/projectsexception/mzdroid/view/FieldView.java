package com.projectsexception.mzdroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.projectsexception.mzdroid.util.FieldUtil;

public class FieldView extends View {
    
    private int fieldH;
    private int fieldW;
    private boolean vertical;

    public FieldView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FieldView(Context context) {
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
            setMeasuredDimension(fieldW, fieldH);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, 
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            params.topMargin = 10;
            setLayoutParams(params);
        } else {
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        
        // Ya tenemos el ancho y alto del campo real, lo pintamos
        drawRect(canvas, 1, 1, fieldW - 1, fieldH - 1, paint);
        
        // Pintamos la línea del centro del campo
        float cy = fieldH / 2;
        drawLine(canvas, 1, cy, fieldW, cy, paint);
        
        // Pinsamos el círculo central
        float cx = fieldW / 2;
        float radius = (float) ((fieldH * FieldUtil.CENTRAL) / FieldUtil.FIELD_HEIGHT);
        drawCircle(canvas, cx, cy, radius, paint);
        
        // Pintamos el punto del centro
        paint.setStyle(Style.FILL_AND_STROKE);
        drawCircle(canvas, cx, cy, 3, paint);
        paint.setStyle(Style.STROKE);
        
        // Pintamos las áreas grandes
        float realX = (float) ((FieldUtil.FIELD_WIDTH - FieldUtil.AREA_WIDTH) / 2);
        float left = (fieldW * realX) / FieldUtil.FIELD_WIDTH;
        float top = 1;
        float right = left + (fieldW * FieldUtil.AREA_WIDTH) / FieldUtil.FIELD_WIDTH;
        float bottom = top + (fieldH * FieldUtil.AREA_HEIGHT) / FieldUtil.FIELD_HEIGHT;
        drawRect(canvas, left, top, right, bottom, paint);
        // Debajo
        top = top + fieldH - bottom - 1;
        bottom = fieldH - 1;
        drawRect(canvas, left, top, right, bottom, paint);
        
        // Area chica
        realX = (float) ((FieldUtil.FIELD_WIDTH - FieldUtil.SMALL_AREA_WIDTH) / 2);
        left = (fieldW * realX) / FieldUtil.FIELD_WIDTH;
        top = 1;
        right = left + (fieldW * FieldUtil.SMALL_AREA_WIDTH) / FieldUtil.FIELD_WIDTH;
        bottom = top + (fieldH * FieldUtil.SMALL_AREA_HEIGHT) / FieldUtil.FIELD_HEIGHT;
        drawRect(canvas, left, top, right, bottom, paint);
        // Debajo
        top = top + fieldH - bottom - 1;
        bottom = fieldH - 1;
        drawRect(canvas, left, top, right, bottom, paint);
        
        // Punto de penal
        cy = (fieldH * FieldUtil.PENAL) / FieldUtil.FIELD_HEIGHT; 
        paint.setStyle(Style.FILL_AND_STROKE);
        drawCircle(canvas, cx, cy, 2, paint);
        cy = fieldH - cy;
        drawCircle(canvas, cx, cy, 2, paint);
        paint.setStyle(Style.STROKE);
        
        // Arco área grande
        realX = (fieldW * FieldUtil.AREA_ARC_RADIO) / FieldUtil.FIELD_WIDTH;
        cy = (fieldH * FieldUtil.PENAL) / FieldUtil.FIELD_HEIGHT;
        left = cx - realX;
        top = cy - realX;
        right = cx + realX;
        bottom = cy + realX;
        drawArc(canvas, left, top, right, bottom, 40, 100, paint);
        cy = fieldH - cy;
        top = cy - realX;
        bottom = cy + realX;
        drawArc(canvas, left, top, right, bottom, 220, 100, paint);
        
        // Corners
        float size = (fieldW * FieldUtil.CORNER_RECT_SIZE) / FieldUtil.FIELD_WIDTH;
        // Izquierda-Arriba
        drawArc(canvas, 0 - size, 0 - size, size, size, 0, 90, paint, false);
        // Derecha-Arriba
        if (vertical) {
            drawArc(canvas, fieldW - size, 0 - size, fieldW + size, size, 90, 90, paint, false);
        } else {
            drawArc(canvas, fieldW - size, 0 - size, fieldW + size, size, 270, 90, paint, false);
        }
        // Izquierda-Abajo
        if (vertical) {
            drawArc(canvas, 0 - size, fieldH - size, size, fieldH + size, 270, 90, paint, false);
        } else {
            drawArc(canvas, 0 - size, fieldH - size, size, fieldH + size, 90, 90, paint, false);
        }
        // Derecha-Abajo
        drawArc(canvas, fieldW - size, fieldH - size, fieldW + size, fieldH + size, 180, 90, paint, false);
    }
    
    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY, Paint paint) {
        if (vertical) {
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        } else {
            canvas.drawLine(startY, startX, stopY, stopX, paint);
        }
    }
    
    private void drawCircle(Canvas canvas, float cx, float cy, float radius, Paint paint) {
        if (vertical) {
            canvas.drawCircle(cx, cy, radius, paint);
        } else {
            canvas.drawCircle(cy, cx, radius, paint);
        }
    }
    
    private void drawRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        canvas.drawRect(buildRectF(left, top, right, bottom), paint);
    }
    
    private RectF buildRectF(float left, float top, float right, float bottom) {
        if (vertical) {
            return new RectF(left, top, right, bottom);
        } else {
            return new RectF(top, left, bottom, right);
        }
    }
    
    private void drawArc(Canvas canvas, float left, float top, float right, float bottom, float startAngle, float sweepAngle, Paint paint, boolean moveAngle) {
        RectF oval = buildRectF(left, top, right, bottom);
        float angle = startAngle;
        if (moveAngle && !vertical) {
            angle = startAngle + 270;
        }
        canvas.drawArc(oval, angle, sweepAngle, false, paint);
    }
    
    private void drawArc(Canvas canvas, float left, float top, float right, float bottom, float startAngle, float sweepAngle, Paint paint) {
        drawArc(canvas, left, top, right, bottom, startAngle, sweepAngle, paint, true);
    }

}
