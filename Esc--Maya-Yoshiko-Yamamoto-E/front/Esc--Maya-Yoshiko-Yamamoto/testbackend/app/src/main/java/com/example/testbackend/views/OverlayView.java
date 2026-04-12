package com.example.testbackend.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testbackend.models.Landmark;

import java.util.List;

public class OverlayView extends View {

    private List<Landmark> landmarks;
    private Paint dotPaint;
    private Paint linePaint;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dotPaint = new Paint();
        dotPaint.setColor(Color.GREEN);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeWidth(12f);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(4f);
    }

    public void setLandmarks(List<Landmark> landmarks) {
        this.landmarks = landmarks;
        invalidate(); // Redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (landmarks == null || landmarks.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();

        for (Landmark landmark : landmarks) {
            float px = landmark.getX() * width;
            float py = landmark.getY() * height;
            canvas.drawCircle(px, py, 8f, dotPaint);
        }
        
        // Note: Real skeletal drawing requires connecting specific landmarks (MediaPipe indices)
        // For testing "SmartSaude", drawing points is the first step.
    }
}