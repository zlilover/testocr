package com.sc.plugin;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFPreviewLayout extends ViewGroup {
    private static final String TAG = LFPreviewLayout.class.getSimpleName();

    private boolean isSurfaceValid;
    private int mPreviewWidth;

    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    private int mPreviewHeight;

    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    SurfaceView mSurfaceView;

    public LFPreviewLayout(Context context) {
        super(context);
        init(context);
    }

    public LFPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LFPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
    }

    public void setPreviewSize(int previewWidth, int previewHeight) {
        mPreviewWidth = previewWidth;
        mPreviewHeight = previewHeight;
        invalidate();
    }

    public SurfaceView getSurfaceView() {
        assert mSurfaceView != null;
        return mSurfaceView;
    }

    public SurfaceHolder getSurfaceHolder() {
        SurfaceHolder holder = getSurfaceView().getHolder();
        assert holder != null;
        return holder;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LFLog.i(TAG, "onLayout", "l", l, "t", t, "r", r, "b", b);
        if (changed && getChildCount() > 0) {
            assert mSurfaceView != null;

            final int width = r - l;
            final int height = b - t;
            if (width * mPreviewHeight > height * mPreviewWidth) {
                final int scaledChildWidth = mPreviewWidth * height / mPreviewHeight;
                mSurfaceView.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = mPreviewHeight * width / mPreviewWidth;
                mSurfaceView.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }
    }
}
