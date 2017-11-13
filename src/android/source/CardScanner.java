package com.sc.plugin;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.linkface.ocr.card.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
@SuppressWarnings("deprecation")
public abstract class CardScanner implements Camera.PreviewCallback, SurfaceHolder.Callback {

    private static final String TAG = CardScanner.class.getSimpleName();

    /**
     * 上下文对象
     */
    protected Context mContext;

    /**
     * OCR扫描回调
     */
    protected LFCardScanListener mCardScanListener;

    /**
     * 当前activity的旋转角度
     */
    protected int mRotation;

    private int mFrameOrientation;

    /**
     * 相机操作类
     */
    private LFCameraProxy mCameraProxy;

    /**
     * 扫描区域大小
     */
    private Rect mCardScanRect;

    /**
     * 预览返回的data
     */
    private byte[][] mPreviewBuffer;

    /**
     * 当前圆方向是否是垂直
     */
    private boolean mCardOrientationVertical = false;

    /**
     * OCR识别类Recognizer
     */
    protected CardRecognizer mCardRecognizer;

    /**
     * 线程池内线程数量
     */
    protected final int mRecognizerNumber = 1;

    /**
     * 线程池
     */
    private ExecutorService mExecutor;

    /**
     * 用于保存当前预览的数据
     */
    private PreviewSaver mPreviewSaver;

    /**
     * 是否需要卡片在框内才可以识别，默认true
     */
    private boolean mIsInFrame = true;

    /**
     * 开始扫描的时间
     */
    private long mStartDetectTime = 0;

    /**
     * 扫描超时时间，单位秒
     */
    private int mScanTimeOut;

    protected CardScanner(Context context, int currentFrameOrientation, boolean cardOrientationVertical) {
        mContext = context;
        mFrameOrientation = currentFrameOrientation;
        this.mCardOrientationVertical = cardOrientationVertical;
        initCameraUtils();
    }

    public void init() {
        try {
            mCardRecognizer = initRecognizer(mContext);
        } catch (RecognizerInitFailException e) {
            e.printStackTrace();
            if (mCardScanListener != null) {
                mCardScanListener.initFail();
            }
        }
    }

    /**
     * 初始化相机操作相关类
     */
    private void initCameraUtils() {
        mCameraProxy = new LFCameraProxy(mContext);
        mCameraProxy.setFrameOrientation(mFrameOrientation);
    }

    /**
     * 创建线程池
     */
    private void createExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newFixedThreadPool(mRecognizerNumber);
        }
    }

    /**
     * 关闭线程池
     */
    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.isShutdown();
            mExecutor = null;
        }
    }

    /**
     * 开启扫描准备
     */
    void prepareScanner() {
        mCameraProxy.openCamera();
    }

    /**
     * 重新开始扫描
     *
     * @param holder
     * @return
     */
    boolean resumeScanning(SurfaceHolder holder) {
        boolean isStart = true;
        if (mCameraProxy.getCamera() == null) {
            prepareScanner();
        }

        if (mCameraProxy.hasCameraPermission(mCameraProxy.getCamera())) {
            createExecutor();
            createPreviewBuffer();

            if (holder != null) {
                holder.addCallback(this);
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mCameraProxy.setPreviewCallbackWithBuffer(this);
                startPreview(holder);
            }
        } else {
            isStart = false;
        }
        mStartDetectTime = System.currentTimeMillis();
        return isStart;
    }

    /**
     * 重置扫描超时的初始时间
     */
    public void resetStartDetectTime(){
        mStartDetectTime = System.currentTimeMillis();
    }

    /**
     * 暂停扫描
     */
    public void pauseScanning() {
        mCameraProxy.releaseCamera();
        mPreviewBuffer = null;
        destroyExecutor();
    }

    /**
     * 停止扫描
     */
    public void endScanning() {
        pauseScanning();
        CardRecognizer cardRecognizer = getCardRecognizer();
        if (cardRecognizer != null) {
            cardRecognizer.destroyRecognizer();
        }
    }

    /**
     * 创建预览buffer
     */
    private void createPreviewBuffer() {
        if (mPreviewBuffer == null) {
            int previewFormat = ImageFormat.NV21; // the default.

            Camera camera = mCameraProxy.getCamera();
            if (camera != null) {

                Camera.Parameters parameters = camera.getParameters();
                previewFormat = parameters.getPreviewFormat();

                int bytesPerPixel = ImageFormat.getBitsPerPixel(previewFormat) / 8;

                int bufferSize = mCameraProxy.getPreviewWidth() * mCameraProxy.getPreviewHeight() * bytesPerPixel * 3 / 2;

                mPreviewBuffer = new byte[mRecognizerNumber][bufferSize];
                for (int i = 0; i < mRecognizerNumber; i++) {
                    mCameraProxy.addCallbackBuffer(mPreviewBuffer[i]);
                }
            }
        }
    }

    /**
     * 开启预览
     *
     * @param holder
     */
    private void startPreview(SurfaceHolder holder) {
        if (holder != null) {
            mCameraProxy.setPreviewDisplay(holder);
            mCameraProxy.startPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCameraProxy.getCamera() != null) {
            startPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCameraProxy.getCamera() != null) {
            startPreview(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCameraProxy != null) {
            mCameraProxy.stopPreview();
        }
    }

    /**
     * 获取OCR识别类
     *
     * @return
     */
    protected CardRecognizer getCardRecognizer() {
        return mCardRecognizer;
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        if (data == null) {
            return;
        }

        if (mPreviewSaver != null) {
            mPreviewSaver.saveBuffer(mContext, (90 * mFrameOrientation) % 360, data);
        }

        if (isCanExecute(mExecutor)) {
            mExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    if (isScanTimeOut()) {
                        dealScanTimeOut();
                        return;
                    } else {
//                        calculateFps();
                        onRecognize(mCardScanRect, data,
                                mCardOrientationVertical);
                    }
                    if (camera != null) {
                        camera.addCallbackBuffer(data);
                    }
                }
            });
        }
    }

    /**
     * 扫描是否超时，
     *
     * @return
     */
    private boolean isScanTimeOut() {
        boolean isTimeOut = false;
        if (mScanTimeOut > 0 && (System.currentTimeMillis() - mStartDetectTime) > mScanTimeOut * 1000) {
            isTimeOut = true;
        }
        return isTimeOut;
    }

    private void dealScanTimeOut() {
        if (mCardScanListener != null) {
            mCardScanListener.scanTimeOut();
        }
    }

    //计算帧率相关变量
    private List<Long> mTimeCounter = new ArrayList<Long>();
    private int mTimeStart = 0;
    private int mFps;

    /**
     * 计算帧率
     */
    public void calculateFps() {
        int fps = 0;
        long timer = System.currentTimeMillis();
        mTimeCounter.add(timer);
        while (mTimeStart < mTimeCounter.size()
                && mTimeCounter.get(mTimeStart) < timer - 1000) {
            mTimeStart++;
        }
        mFps = mTimeCounter.size() - mTimeStart;
        if (mTimeStart > 100) {
            mTimeCounter = mTimeCounter.subList(mTimeStart,
                    mTimeCounter.size() - 1);
            mTimeStart = 0;
        }
        LFLog.i(TAG, "calculateFps", "mFps", mFps);
    }

    /**
     * 识别图片
     *
     * @param scanRect   扫描区域大小
     * @param srcByte    扫描区域图片
     * @param isVertical 是否是垂直
     */
    protected void onRecognize(final Rect scanRect, final byte[] srcByte, final boolean isVertical) {
        CardRecognizer cardRecognizer = getCardRecognizer();
        final int rotateDegree = (90 * mFrameOrientation) % 360;
        if (cardRecognizer != null) {
            Rect clipRect = new Rect(0, 0, getPreviewWidth(), getPreviewHeight());
            if (scanRect != null) {
                if (rotateDegree == 0 || rotateDegree == 180) {
                    clipRect = new Rect(scanRect.left, scanRect.top, scanRect.right, scanRect.bottom);
                } else if (rotateDegree == 90 || rotateDegree == 270) {
                    clipRect = new Rect(scanRect.top, scanRect.left, scanRect.bottom, scanRect.right);
                }
            }

            final byte[] cropByte = cardRecognizer.clipNv21Byte(srcByte, clipRect, getPreviewWidth(), getPreviewHeight());
            final byte[] scanByte = getPreviewScanByte(rotateDegree, cropByte, clipRect.width(), clipRect.height());
            cardRecognizer.recognizeCard(scanByte, scanRect.width(), scanRect.height(), null, isVertical, mIsInFrame,
                    new CardRecognizer.ICardRecognizeCallback() {
                        @Override
                        public void callback(Card card, Bitmap recognizeBitmap) {
                            if (card != null) {
                                if (mCardScanListener != null) {
                                    Bitmap cropBitmap = getScanBitmap(scanByte, scanRect.width(), scanRect.height());
                                    mCardScanListener.onCardDetected(card, cropBitmap, recognizeBitmap);
                                }
                            }
                        }
                    });
        }
    }

    private int[] getPreviewScanSize(int rotateDegree) {
        int[] scanSize = {getPreviewWidth(), getPreviewHeight()};
        if (rotateDegree == 90) {
            scanSize[0] = getPreviewHeight();
            scanSize[1] = getPreviewWidth();
        } else if (rotateDegree == 180) {
        } else if (rotateDegree == 270) {
            scanSize[0] = getPreviewHeight();
            scanSize[1] = getPreviewWidth();
        }
        return scanSize;
    }

    private byte[] getPreviewScanByte(int rotateDegree, byte[] srcData, int width, int height) {
        byte[] scanData = srcData;
        if (rotateDegree == 90) {
            scanData = LFImageUtils.rotateYUV420Degree90(srcData, width, height);
        } else if (rotateDegree == 180) {
            scanData = LFImageUtils.rotateYUV420Degree180(srcData, width, height);
        } else if (rotateDegree == 270) {
            scanData = LFImageUtils.rotateYUV420Degree270(srcData, width, height);
        }
        return scanData;
    }

    /**
     * 当前线程池是否可执行
     *
     * @param executor
     */
    private boolean isCanExecute(ExecutorService executor) {
        return executor != null && !executor.isShutdown();
    }

    /**
     * 根据相机返回的预览数据流获取进行识别的图像
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    private Bitmap getScanBitmap(byte[] data, int width, int height) {
        Bitmap scanBitmap = Util.NV21ToRGBABitmap(data, width, height, mContext);
        return scanBitmap;
    }

    /**
     * 设置OCR扫描回调
     *
     * @param cardScanListener
     */
    public void setCardScanListener(LFCardScanListener cardScanListener) {
        this.mCardScanListener = cardScanListener;
    }

    /**
     * 设置当前activity的旋转角度
     *
     * @param rotation
     */
    public void setRotation(int rotation) {
        this.mRotation = rotation;
        if (mCameraProxy != null) {
            mCameraProxy.setRotation(rotation);
        }
    }

    /**
     * 设置识别的方向
     *
     * @param cardOrientationVertical
     */
    protected void setCardOrientationVertical(boolean cardOrientationVertical) {
        mCardOrientationVertical = cardOrientationVertical;
    }

    /**
     * 获取预览的宽
     *
     * @return
     */
    public int getPreviewWidth() {
        return mCameraProxy != null ? mCameraProxy.getPreviewWidth() : 1280;
    }

    /**
     * 获取预览的高
     *
     * @return
     */
    public int getPreviewHeight() {
        return mCameraProxy != null ? mCameraProxy.getPreviewHeight() : 960;
    }

    /**
     * 设置扫描区域大小
     *
     * @param cardScanRect
     */
    public void setCardScanRect(Rect cardScanRect) {
        this.mCardScanRect = new Rect(cardScanRect);
        mCardScanRect.left &= ~1;
        mCardScanRect.right &= ~1;
        mCardScanRect.top &= ~1;
        mCardScanRect.bottom &= ~1;
    }

    protected abstract CardRecognizer initRecognizer(Context context) throws RecognizerInitFailException;

    public void setPreviewInfo(int previewTimeGaps, String previewStoragePath, int savedNum) {
        if (previewTimeGaps > 0 && previewStoragePath != null) {
            mPreviewSaver = new PreviewSaver(previewTimeGaps, previewStoragePath, savedNum);
            mPreviewSaver.setPreviewSize(getPreviewWidth(), getPreviewHeight());
        } else {
            mPreviewSaver = null;
        }
    }

    public void setIsInFrame(boolean isInFrame) {
        this.mIsInFrame = isInFrame;
    }

    public void setScanTimeOut(int scanTimeOut) {
        this.mScanTimeOut = scanTimeOut;
    }
}

