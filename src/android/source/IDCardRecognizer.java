package com.sc.plugin;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.linkface.ocr.idcard.IDCard;
import com.linkface.ocr.idcard.LFIDCardScan;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class IDCardRecognizer extends CardRecognizer {

    private static final String TAG = IDCardRecognizer.class.getSimpleName();

    /**
     * 身份证识别模式
     */
    public enum Mode {
        /**
         * 仅识别身份证正面
         */
        FRONT(1),
        /**
         * 仅识别身份证背面
         */
        BACK(2),
        /**
         * 身份证正反两面都尝试
         *
         * @deprecated 请使用{@link #SMART}
         */
        BOTH(0),
        /**
         * 身份证正面反面智能检测
         */
        SMART(0);

        /**
         * 映射到jni层的类型
         */
        private int mDetectVal = -1;

        Mode(int val) {
            mDetectVal = val;
        }

        public int getValue() {
            return mDetectVal;
        }
    }

    /**
     * 身份证识别模式
     */
    private Mode mode;

    /**
     * 识别所有信息
     */
    public static final int RECOGNIZE_FLAG_ALL = 0;
    /**
     * 识别姓名
     */
    public static final int RECOGNIZE_FLAG_NAME = 1 << 0;
    /**
     * 识别性别
     */
    public static final int RECOGNIZE_FLAG_SEX = 1 << 1;
    /**
     * 识别民族
     */
    public static final int RECOGNIZE_FLAG_NATION = 1 << 2;
    /**
     * 识别生日
     */
    public static final int RECOGNIZE_FLAG_BIRTH = 1 << 3;
    /**
     * 识别地址
     */
    public static final int RECOGNIZE_FLAG_ADDR = 1 << 4;
    /**
     * 识别身份证号
     */
    public static final int RECOGNIZE_FLAG_ID = 1 << 5;
    /**
     * 识别签发机关
     */
    public static final int RECOGNIZE_FLAG_AUTHORITY = 1 << 6;
    /**
     * 识别有效期限
     */
    public static final int RECOGNIZE_FLAG_VALIDITY = 1 << 7;

    /**
     * 需要识别的的身份证字段Mask
     */
    private int mRecognizeFlag;

    /**
     * 是否是第一次识别,jni层需要使用
     */
    private boolean mIsFirstRecognize = true;

    /**
     * IDCardOffLine 初始化函数
     * <p>
     * 在使用身份证识别功能之前调用，可以初始化一次，多次进行身份证识别。
     * <p>
     * 如果数据完整，初始化成功，返回 STIDCard 对象；否则返回 null 。
     *
     * @param context {@link android.app.Activity#getApplicationContext()}
     * @throws RecognizerInitFailException createInstanceFail抛出此异常
     */
    public IDCardRecognizer(Context context) throws RecognizerInitFailException {
        super(context);
    }

    @Override
    protected void recognizeCard(byte[] cardByte, int width, int height, Rect scanRect,
                                 boolean isVertical, boolean isInFrame, ICardRecognizeCallback cardRecognizeCallback) {
        IDCard idCard = null;
        Bitmap recognizeBitmap = null;
        if (cardByte != null) {
            Mode mode = getMode();
            if (mode != null) {
                long startTime = System.currentTimeMillis();
                idCard = LFIDCardScan.scanIDCard(mode.getValue(), cardByte, width, height,
                        null, this.mRecognizeFlag, mIsFirstRecognize, isInFrame);
                long endTime = System.currentTimeMillis();
                LFLog.i(TAG, "recognizeCard", "scanIDCard", (endTime - startTime));
                if (idCard != null) {
                    recognizeBitmap = LFImageUtils.getBitmap(idCard.getRectifiedImage(), 1280, 800);
                }
                mIsFirstRecognize = false;
            }
        }

        if (cardRecognizeCallback != null) {
            cardRecognizeCallback.callback(idCard, recognizeBitmap);
        }
    }

    /**
     * 获取当前身份证识别模式
     *
     * @return 当前识别模式
     * @see #setMode(Mode)
     */
    public Mode getMode() {
        if (mode == null)
            return Mode.SMART;
        return this.mode;
    }

    /**
     * 设置身份证识别模式 {@link Mode#SMART}(default)
     *
     * @param mode 新的识别模式
     */
    public void setMode(Mode mode) {
        if (mode == null)
            this.mode = Mode.SMART;
        else
            this.mode = mode;
    }

    /**
     * 获取需要识别的身份证字段Mask
     *
     * @return 当前识别字段Mask
     * @see #setRecognizeFlag(int)
     */
    public int getRecognizeFlag() {
        return this.mRecognizeFlag;
    }

    /**
     * 设置需要识别的的身份证字段Mask
     *
     * @param flag 新的识别字段Mask {@link #RECOGNIZE_FLAG_ALL}(default)
     *             {@link #RECOGNIZE_FLAG_NAME} {@link #RECOGNIZE_FLAG_SEX}
     *             {@link #RECOGNIZE_FLAG_NATION} {@link #RECOGNIZE_FLAG_BIRTH}
     *             {@link #RECOGNIZE_FLAG_ADDR} {@link #RECOGNIZE_FLAG_ID}
     *             {@link #RECOGNIZE_FLAG_AUTHORITY}
     *             {@link #RECOGNIZE_FLAG_VALIDITY}
     */
    public void setRecognizeFlag(int flag) {
        this.mRecognizeFlag = flag;
    }

    /**
     * 初始化IDCard识别类
     *
     * @param licenseName
     * @return
     */
    @Override
    protected boolean initRecognizer(String licenseName) {
        return LFIDCardScan.initIDCardScan(getContext(), licenseName);
    }

    @Override
    public byte[] clipNv21Byte(byte[] nv21Byte, Rect rect, int width, int height) {
        return LFIDCardScan.clipNv21Byte(nv21Byte, rect, width, height);
    }

    /**
     * 销毁OCR识别
     */
    @Override
    protected void destroyRecognizer() {
        LFIDCardScan.releaseIDCardScan();
    }
}
