package com.sc.plugin;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFCardImageView extends ImageView {
    public LFCardImageView(Context context) {
        super(context);
    }

    public LFCardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LFCardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        originalHeight = (int) ((originalWidth + 0.0f) * 5 / 8);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(originalHeight, MeasureSpec.EXACTLY));
    }
}
