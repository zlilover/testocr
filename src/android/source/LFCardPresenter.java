package com.sc.plugin;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Surface;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFCardPresenter extends LFBasePresenter {

    private static final String TAG = "LFCardPresenter";

    /**
     * 根据屏幕方向获取预览尺寸
     *
     * @param frameOrientation 屏幕方向
     * @param previewWidth     预览宽
     * @param previewHeight    预览高
     * @return 返回预览尺寸数组，0--宽，1--高
     */
    public int[] getPreviewSize(int frameOrientation, int previewWidth, int previewHeight) {
        int[] previewSize = new int[2];
        switch (frameOrientation) {
            case CardActivity.ORIENTATION_LANDSCAPE_LEFT:
            case CardActivity.ORIENTATION_LANDSCAPE_RIGHT:
                previewSize[0] = previewWidth;
                previewSize[1] = previewHeight;
                break;
            case CardActivity.ORIENTATION_PORTRAIT:
            case CardActivity.ORIENTATION_PORTRAIT_UPSIDE_DOWN:
            default:
                previewSize[0] = previewHeight;
                previewSize[1] = previewWidth;
                break;
        }
        return previewSize;
    }

    /**
     * 获取当前activity的旋转方向
     *
     * @param activity
     * @return
     */
    public int getRotation(Activity activity) {
        int rotation = Surface.ROTATION_0;
        if (activity != null) {
            rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        }
        return rotation;
    }

    /**
     * 储存扫描区域大小Rect的map，防止重新创建
     */
    private Map<String, Rect> mCardScanRectMap = new HashMap<String, Rect>();

    /**
     * 获取OCR扫描区域大小,比例---- 8：5
     *
     * @param previewWidth
     * @param previewHeight
     * @return
     */
    public Rect getCardScanRect(int frameOrientation, boolean cardOrientationVertical, final int previewWidth, final int previewHeight) {

        String key = String.format(Locale.US, "%b_%d_%d_%d", cardOrientationVertical, previewWidth, previewHeight, frameOrientation);

        LFLog.i(TAG, "getCardScanRect", "key", key);

        if (mCardScanRectMap.get(key) == null) {
            Rect scanRect = new Rect();

            int rectWidth = 0;
            int rectHeight = 0;

            switch (frameOrientation) {
                case CardActivity.ORIENTATION_LANDSCAPE_LEFT:
                case CardActivity.ORIENTATION_LANDSCAPE_RIGHT:
                    if (cardOrientationVertical) {
                        rectHeight = (int) ((previewHeight + 0.0f) * 8 / 10);
                        rectWidth = (int) ((rectHeight + 0.0f) * 5 / 8);
                    } else {
                        rectWidth = (int) ((previewWidth + 0.0f) * 6 / 10);
                        rectHeight = (int) ((rectWidth + 0.0f) * 5 / 8);
                    }
                    break;
                case CardActivity.ORIENTATION_PORTRAIT:
                case CardActivity.ORIENTATION_PORTRAIT_UPSIDE_DOWN:
                default:
                    if (cardOrientationVertical) {
                        rectHeight = (int) ((previewHeight + 0.0f) * 7 / 10);
                        rectWidth = (int) ((rectHeight + 0.0f) * 5 / 8);
                    } else {
                        rectWidth = (int) ((previewWidth + 0.0f) * 9 / 10);
                        rectHeight = (int) ((rectWidth + 0.0f) * 5 / 8);
                    }
                    break;
            }

            scanRect.left = (previewWidth - rectWidth) / 2;
            scanRect.right = scanRect.left + rectWidth;
            scanRect.top = (previewHeight - rectHeight) / 2;
            scanRect.bottom = scanRect.top + rectHeight;

            mCardScanRectMap.put(key, scanRect);
        }

        Rect rect = mCardScanRectMap.get(key);
        return new Rect(rect);
    }

    public Rect rotationRect90(Rect scanRect) {
        int width = scanRect.width();
        int height = scanRect.height();
        int left = width / 2 + scanRect.left - height / 2;
        int top = height / 2 + scanRect.top - width / 2;
        scanRect.left = left;
        scanRect.right = left + height;
        scanRect.top = top;
        scanRect.bottom = top + width;
        return scanRect;
    }


    private static final long[] VIBRATE_PATTERN = {0, 70, 10, 40};

    /**
     * 播放震动效果
     *
     * @param context
     */
    public void playVibrator(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_PATTERN, -1);
        } catch (SecurityException e) {
            Log.e(Util.PUBLIC_LOG_TAG,
                    "无法使用Vibrator，请在manifest中添加 <uses-permission android:name=\"android.permission.VIBRATE\" />");
        } catch (Exception e) {
            Log.w(Util.PUBLIC_LOG_TAG, "vibrate error:", e);
        }
    }
}
