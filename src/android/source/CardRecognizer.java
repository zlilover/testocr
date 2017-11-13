package com.sc.plugin;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.linkface.ocr.card.Card;


/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public abstract class CardRecognizer {

    private static final String TAG = CardRecognizer.class.getSimpleName();

    protected static final String LICENSE_NAME = "SenseID_OCR.lic";

    protected Context mContext;

    /**
     * CardRecognizer 初始化函数
     * <p>
     * 在使用Card识别功能之前调用, 可以初始化一次，多次进行Card识别。
     *
     * @param context {@link android.app.Activity#getApplicationContext()}
     * @throws RecognizerInitFailException createInstanceFail抛出此异常
     */
    public CardRecognizer(Context context) throws RecognizerInitFailException {
        this.mContext = context;
        init();
    }

    private void init() throws RecognizerInitFailException {
        if (!initRecognizer(LICENSE_NAME)) {
            throw new RecognizerInitFailException();
        }
    }

    /**
     * 识别图片
     *
     * @param cardByte
     * @param scanRect
     * @param isVertical
     * @param isInFrame  卡片是否在框内才可以被识别
     * @return
     */
    protected abstract void recognizeCard(byte[] cardByte, int width, int height, Rect scanRect, boolean isVertical,
                                          boolean isInFrame, ICardRecognizeCallback cardRecognizeCallback);

    /**
     * 初始化OCR识别类
     *
     * @param licenseName
     * @return
     */
    protected abstract boolean initRecognizer(String licenseName);

    public abstract byte[] clipNv21Byte(byte[] nv21Byte, Rect rect, int width, int height);

    /**
     * 释放OCR识别资源
     */
    protected abstract void destroyRecognizer();

    public Context getContext() {
        return mContext;
    }

    public interface ICardRecognizeCallback {
        /**
         * 识别结果
         *
         * @param card            OCR识别结果信息
         * @param recognizeBitmap OCR识别结果裁剪后的图片
         */
        void callback(Card card, Bitmap recognizeBitmap);
    }
}
