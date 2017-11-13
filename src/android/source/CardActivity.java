package com.sc.plugin;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkface.ocr.card.Card;

import java.io.ByteArrayOutputStream;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public abstract class CardActivity extends Activity implements SurfaceHolder.Callback, LFCardScanListener {

    private static final String TAG = CardActivity.class.getSimpleName();

    /**
     * 默认的扫描框颜色
     */
    private static final int SCAN_RECT_DEFAULT_COLOR = 0xffffffff;
    /**
     * 默认的背景颜色
     */
    private final int BACKGROUND_DEFAULT_COLOR = 0xbb000000;
    /**
     * 扫描框偏移量设置，没有使用，暂时保留
     */
    public static int SCANRECTOFFSET = 0;


    /**
     * 扫描界面返回按钮背景图片资源ID
     */
    public static final String EXTRA_BACK_DRAWABLE_ID = "com.linkface.card.backDrawable";

    /**
     * 扫描界面标题
     */
    public static final String EXTRA_SCAN_TITLE = "com.linkface.card.title";

    /**
     * 扫描界面标题标题部分右侧图标按钮---横屏切换到竖屏资源ID
     */
    public static final String EXTRA_TITLE_RIGHT_HORIZONTAL_TO_VERTICAL_DRAWABLE_ID = "com.linkface.card.title.right.to.vertical";

    /**
     * 扫描界面标题标题部分右侧图标按钮---横屏切换到竖屏资源ID
     */
    public static final String EXTRA_TITLE_RIGHT_VERTICAL_TO_HORIZONTAL_DRAWABLE_ID = "com.linkface.card.title.right.to.horizontal";

    /**
     * 扫描框颜色
     */
    public static final String EXTRA_SCAN_GUIDE_COLOR = "com.linkface.card.guideColor";

    /**
     * 背景颜色
     */
    public static final String EXTRA_SCAN_BACKGROUND_COLOR = "com.linkface.card.backgroundColor";

    /**
     * 获取扫描结果
     */
    public static final String EXTRA_SCAN_RESULT = "com.linkface.card.scanResult";

    /**
     * 用于传入保存预览图像的频率的key（单位s）
     */
    public static final String EXTRA_PREVIEW_TIME_GAPS = "com.linkface.card.preview.time.gaps";

    /**
     * 用于传入保存预览图像的路径的key
     */
    public static final String EXTRA_PREVIEW_STOREAGE_PATH = "com.linkface.card.preview.storage.path";

    /**
     * 用于传入保存预览图像的最大个数的key
     */
    public static final String EXTRA_PREVIEW_SAVED_NUM = "com.linkface.card.preview.saved.num";

    /**
     * 设置扫描界面提示文字<br>
     * 输入类型为String，用\n换行
     */
    public static final String EXTRA_SCAN_TIPS = "com.linkface.card.scanTips";

    /**
     * 用于传入扫描偏移量，参考SCANRECTOFFSET
     */
    public static final String EXTRA_SCAN_RECT_OFFSET = "com.linkface.card.scanRectOffset";

    /**
     * 用于传入当前的识别方向  true---垂直，false-水平
     */
    public static final String EXTRA_SCAN_CARD_VERTICAL = "com.linkface.card.vertical";

    /**
     * 设置需要输出原始图片<br>
     * 输入类型为boolean，true表示要输出，false表示不需要输出<br>
     * 可以在{@link Activity#onActivityResult(int, int, Intent)}中使用<br>
     * data.getByteArrayExtra(CardActivity.EXTRA_CARD_IMAGE)获取JPEG字节数组
     */
    public static final String EXTRA_CARD_IMAGE = "com.linkface.card.image";

    /**
     * 设置需要输出裁剪之后的图片<br>
     * 输入类型为boolean，true表示要输出，false表示不需要输出<br>
     * 可以在{@link Activity#onActivityResult(int, int, Intent)}中使用<br>
     * data.getByteArrayExtra(CardActivity.EXTRA_CARD_IMAGE_RECTIFIED)获取JPEG字节数组
     */
    public static final String EXTRA_CARD_IMAGE_RECTIFIED = "com.linkface.card.rectifiedImage";

    /**
     * 需要双面扫描时使用，设置需要输出裁剪之后的身份证正面图片<br>
     * 输入类型为boolean，true表示要输出，false表示不需要输出<br>
     * 可以在{@link Activity#onActivityResult(int, int, Intent)}中使用<br>
     * data.getByteArrayExtra(CardActivity.EXTRA_CARD_IMAGE_FRONT_RECTIFIED)获取JPEG字节数组
     */
    public static final String EXTRA_CARD_IMAGE_FRONT_RECTIFIED = "com.linkface.card.rectifiedImageFront";

    /**
     * 需要双面扫描时使用，设置需要输出的身份证正面原图<br>
     * 输入类型为boolean，true表示要输出，false表示不需要输出<br>
     * 可以在{@link Activity#onActivityResult(int, int, Intent)}中使用<br>
     * data.getByteArrayExtra(CardActivity.EXTRA_CARD_IMAGE_FRONT_RECTIFIED)获取JPEG字节数组
     */
    public static final String EXTRA_CARD_IMAGE_FRONT = "com.linkface.card.imageFront";

    /**
     * 设置扫描界面的方向<br>
     * {@link #ORIENTATION_PORTRAIT}<br>
     * {@link #ORIENTATION_LANDSCAPE_LEFT}<br>
     * {@link #ORIENTATION_LANDSCAPE_RIGHT}<br>
     */
    public static final String EXTRA_SCAN_ORIENTATION = "com.linkface.card.orientation";

    public static final String EXTRA_SCAN_LINE_STATUS = "com.linkface.card.scan.line.status";

    /**
     * 是否需要卡片在框内才可以识别的传入key
     */
    public static final String EXTRA_SCAN_IS_IN_FRAME = "com.linkface.card.scan.is.in.frame";

    /**
     * 设置扫描的超时时间，单位秒
     */
    public static final String EXTRA_SCAN_TIME_OUT = "com.linkface.card.scan.time.out";

    /**
     * 卡片信息返回成功
     */
    public static final int RESULT_CARD_INFO = 1;
    /**
     * 相机无法使用
     */
    public static final int RESULT_CAMERA_NOT_AVAILABLE = 2;
    /**
     * 初始化失败，可能不兼容或模型不匹配
     */
    public static final int RESULT_RECOGNIZER_INIT_FAILED = 3;

    /**
     * 扫描失败，扫描超时
     */
    public static final int RESULT_RECOGNIZER_FAIL_SCAN_TIME_OUT = 4;

    /**
     * 面对屏幕，设备头部朝上
     */
    public static final int ORIENTATION_PORTRAIT = 1;
    /**
     * 面对屏幕，设备头部朝右
     */
    public static final int ORIENTATION_LANDSCAPE_RIGHT = 2;
    // TODO-Alen Portrait upside down is not working, make it public after we've
    // fixed this issue
    public static final int ORIENTATION_PORTRAIT_UPSIDE_DOWN = 3;
    /**
     * 面对屏幕，设备头部朝左
     */
    public static final int ORIENTATION_LANDSCAPE_LEFT = 4;

    /**
     * 扫描的默认超时时间，30s
     */
    private static final int LF_SCAN_TIME_OUT_DEFAULT = 30;

    /**
     * 当前识别是否是垂直方向
     */
    protected boolean mCardOrientationVertical = false;

    /**
     * 预览view
     */
    private LFPreviewLayout mPlytPreview;

    /**
     * 预览页面覆盖层
     */
    private View mOverlay;

    private LFCardPresenter mCardPresenter;

    /**
     * 扫描区域大小
     */
    private Rect mCardScanRect;

    /**
     * 当前识别的方向
     */
    private int mFrameOrientation;

    /**
     * 默认的背景颜色
     */
    private int mBackGroundColor;

    /**
     * 默认的扫描框颜色
     */
    private int mScanRectColor;

    /**
     * OCR扫描类
     */
    private CardScanner mCardScanner;

    /**
     * 预览覆盖层
     */
    private FrameLayout mPreviewFrameLayout;

    /**
     * 标题右侧按钮
     */
    protected ImageView mIvTitleRightBtn;

    /**
     * 是否开启扫描光标，默认开启
     */
    private boolean mIsStartScanLine;

    /**
     * 标题右边图标，跳转到竖屏
     */
    private int mRightTitleToVerticalResId;

    /**
     * 标题右边图标，跳转到横屏
     */
    private int mRightTitleToHorizontalResId;

    private Handler mMainHandler;

    /**
     * 是否需要卡片在框内才可以识别，默认true
     */
    private boolean mIsInFrame = true;

    /**
     * 扫描的超时时间
     */
    private int mScanTimeOut;

    @Override
    @SuppressLint("InlinedApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(MyResource.getIdByName(this,"layout","layout_card_activity_main"));
        LFLog.i(TAG, "onCreate");

        mMainHandler = new Handler();

        initPresenter();
        getIntentData();
        try {
            createCardScan();
            initView();
            initOrientation();
        } catch (Exception e) {
            handleGeneralExceptionError(e);
        }

    }

    private void initPresenter() {
        mCardPresenter = new LFCardPresenter();
    }

    /**
     * 初始化布局
     */
    protected void initView() {

        initPreviewView();
        initBackButton();
        initTitle();
        initTitleRightBtn();
    }

    /**
     * 初始化预览界面
     */
    private void initPreviewView() {
        mPlytPreview = (LFPreviewLayout) findViewById(MyResource.getIdByName(this,"id","id_plyt_preview"));

        int[] previewSize = mCardPresenter.getPreviewSize(mFrameOrientation, mCardScanner.getPreviewWidth(),
                mCardScanner.getPreviewHeight());
        if (previewSize != null) {
            mPlytPreview.setPreviewSize(previewSize[0], previewSize[1]);
        }

        mPlytPreview.getSurfaceHolder().addCallback(this);
    }

    /**
     * 初始化返回按钮
     */
    private void initBackButton() {
        //返回按钮
        ImageView ivBack = (ImageView) findViewById(MyResource.getIdByName(this,"id","id_iv_back"));
        int backDrawableID = getIntent().getIntExtra(EXTRA_BACK_DRAWABLE_ID, 0);

        if (backDrawableID != 0) {
            ivBack.setImageResource(backDrawableID);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBack(view);
                }
            });
        }
    }


    /**
     * 返回按钮点击事件
     *
     * @param view
     */
    protected void onClickBack(View view) {
        finish();
    }

    /**
     * 初始化标题
     */
    protected void initTitle() {
        TextView tvTitle = (TextView) findViewById(MyResource.getIdByName(this,"id","id_tv_title"));
        String title = getIntent().getStringExtra(EXTRA_SCAN_TITLE);
        tvTitle.setText(title);
    }

    /**
     * 初始化标题栏右侧按钮图标
     */
    protected void initTitleRightBtn() {
        mIvTitleRightBtn = (ImageView) findViewById(MyResource.getIdByName(this,"id","id_iv_right"));
        refreshRightTitleBtnView();
        mIvTitleRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTitleRightBtn(view);
            }
        });
    }

    private int getRightButtonResId() {
        return mCardOrientationVertical ? mRightTitleToHorizontalResId : mRightTitleToVerticalResId;
    }

    protected void refreshRightTitleBtnView() {
        int resId = getRightButtonResId();
        if (mIvTitleRightBtn != null && resId != 0) {
            mIvTitleRightBtn.setImageResource(resId);
        }
    }

    /**
     * 标题栏右侧按钮点击事件
     */
    protected void onClickTitleRightBtn(View view) {

    }

    /**
     * 初始化预览界面覆盖层
     */
    protected void initOverlayView() {
        mPreviewFrameLayout = (FrameLayout) findViewById(MyResource.getIdByName(this,"id","id_vs_overlay"));

        SurfaceView sv = mPlytPreview.getSurfaceView();
        if (sv == null) {
            return;
        }

        mCardScanRect = getCardScanRect(mFrameOrientation, mCardOrientationVertical, sv.getWidth(), sv.getHeight());

        mCardScanRect.top += sv.getTop();
        mCardScanRect.bottom += sv.getTop();
        mCardScanRect.left += sv.getLeft();
        mCardScanRect.right += sv.getLeft();

        if (mCardScanner != null) {
            float scale = getCropRectScale(mCardScanner.getPreviewWidth(), sv.getWidth(), sv.getHeight());
            Rect cropRect = new Rect();
            cropRect.left = (int) (mCardScanRect.left * scale);
            cropRect.right = (int) (mCardScanRect.right * scale);
            cropRect.top = (int) (mCardScanRect.top * scale);
            cropRect.bottom = (int) (mCardScanRect.bottom * scale);
            mCardScanner.setCardScanRect(cropRect);
        }

        mOverlay = createOverlayView();

        if (mOverlay instanceof OverlayView) {
            ((OverlayView) mOverlay).setPreviewAndScanRect(new Rect(sv.getLeft(), sv.getTop(), sv.getRight(), sv.getBottom()),
                    mCardScanRect);
        }

        addOverlayView(mOverlay);
    }

    private float getCropRectScale(int previewWidth, int surfaceWidth, int surfaceHeight) {
        float scale = 1;
        switch (mFrameOrientation) {
            case ORIENTATION_LANDSCAPE_LEFT:
            case ORIENTATION_LANDSCAPE_RIGHT:
                scale = (previewWidth + 0.0f) / surfaceWidth;
                break;
            case ORIENTATION_PORTRAIT:
            case ORIENTATION_PORTRAIT_UPSIDE_DOWN:
            default:
                scale = (previewWidth + 0.0f) / surfaceHeight;
                break;
        }
        return scale;
    }

    /**
     * 清除预览覆盖层view
     */
    protected void clearOverlayView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPreviewFrameLayout != null) {
                    mPreviewFrameLayout.removeAllViews();
                }
            }
        });
    }

    /**
     * 添加到预览覆盖层
     *
     * @param view
     */
    protected void addOverlayView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                mPreviewFrameLayout.removeView(view);
                mPreviewFrameLayout.addView(view, layoutParams);
            }
        });
    }

    /**
     * 获取扫描区域大小
     *
     * @param width
     * @param height
     * @return
     */
    private Rect getCardScanRect(int frameOrientation, boolean cardOrientationVertical, int width, int height) {
        Rect rect = new Rect();
        if (mCardPresenter != null) {
            rect = mCardPresenter.getCardScanRect(frameOrientation, cardOrientationVertical, width, height);
        }
        return rect;
    }

    /**
     * 获取数据
     */
    private void getIntentData() {
        mCardOrientationVertical = getIntent().getBooleanExtra(EXTRA_SCAN_CARD_VERTICAL, false);
//        mFrameOrientation = getIntent().getIntExtra(EXTRA_SCAN_ORIENTATION, ORIENTATION_PORTRAIT);

        mBackGroundColor = getIntent().getIntExtra(EXTRA_SCAN_BACKGROUND_COLOR, BACKGROUND_DEFAULT_COLOR);
        mScanRectColor = getIntent().getIntExtra(EXTRA_SCAN_GUIDE_COLOR, SCAN_RECT_DEFAULT_COLOR);
        mIsStartScanLine = getIntent().getBooleanExtra(EXTRA_SCAN_LINE_STATUS, true);

        mRightTitleToHorizontalResId = getIntent().getIntExtra(EXTRA_TITLE_RIGHT_VERTICAL_TO_HORIZONTAL_DRAWABLE_ID, 0);
        mRightTitleToVerticalResId = getIntent().getIntExtra(EXTRA_TITLE_RIGHT_HORIZONTAL_TO_VERTICAL_DRAWABLE_ID, 0);

        mIsInFrame = getIntent().getBooleanExtra(EXTRA_SCAN_IS_IN_FRAME, true);
        mScanTimeOut = getIntent().getIntExtra(EXTRA_SCAN_TIME_OUT, LF_SCAN_TIME_OUT_DEFAULT);


        if (mCardOrientationVertical) {
            mFrameOrientation = ORIENTATION_PORTRAIT;
        } else {
            mFrameOrientation = getIntent().getIntExtra(EXTRA_SCAN_ORIENTATION, ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 初始化界面的方向
     */
    private void initOrientation() {
        switch (mFrameOrientation) {
            case ORIENTATION_PORTRAIT:
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE_LEFT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case ORIENTATION_PORTRAIT_UPSIDE_DOWN:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE_RIGHT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }

        SCANRECTOFFSET = getIntent().getIntExtra(EXTRA_SCAN_RECT_OFFSET, 0);
        if (SCANRECTOFFSET > 75) {
            SCANRECTOFFSET = 75;
        }
        if (SCANRECTOFFSET < -75) {
            SCANRECTOFFSET = -75;
        }

    }

    /**
     * 创建OCR扫描类
     */
    protected void createCardScan() {
        int previewTimeGaps = getIntent().getIntExtra(EXTRA_PREVIEW_TIME_GAPS, -1);
        String previewStoragePath = getIntent().getStringExtra(EXTRA_PREVIEW_STOREAGE_PATH);
        int savedNum = getIntent().getIntExtra(EXTRA_PREVIEW_SAVED_NUM, 3);

        mCardScanner = initCardScanner(this, mFrameOrientation, mCardOrientationVertical);
        mCardScanner.setCardScanListener(this);
        mCardScanner.init();
        mCardScanner.setIsInFrame(mIsInFrame);
        mCardScanner.setScanTimeOut(mScanTimeOut);
        mCardScanner.setRotation(mCardPresenter.getRotation(this));

        mCardScanner.prepareScanner();
        mCardScanner.setPreviewInfo(previewTimeGaps, previewStoragePath, savedNum);

        mCardScanRect = new Rect();
    }

    /**
     * 暂停OCR扫描
     */
    private void pauseCardScan() {
        if (mCardScanner != null) {
            mCardScanner.pauseScanning();
        }
    }

    /**
     * 释放OCR扫描
     */
    private void releaseCardScan() {
        if (mCardScanner != null) {
            mCardScanner.endScanning();
            mCardScanner = null;
        }
    }

    /**
     * 重新开始OCR扫描
     */
    private boolean restartCardScan() {
        if (mPlytPreview == null || mCardScanner == null) {
            return false;
        }
        return mCardScanner.resumeScanning(mPlytPreview.getSurfaceHolder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!restartCardScan()) {
            LFLog.e(TAG, "无法打开摄像头");
            setResultAndFinish(RESULT_CAMERA_NOT_AVAILABLE, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseCardScan();
    }

    @Override
    protected void onDestroy() {
        mOverlay = null;
        releaseCardScan();
        super.onDestroy();
        mPlytPreview = null;
    }

    private void handleGeneralExceptionError(Exception e) {
        Log.e(Util.PUBLIC_LOG_TAG, "发生未知异常，请与我们联系 https://www.linkface.cn", e);
    }

    /**
     * 创建扫描覆盖层
     *
     * @return Overlay View
     */
    protected View createOverlayView() {
        OverlayView overlay = new OverlayView(this, null);
        overlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (getIntent() != null) {
            String scanInstructions = getIntent().getStringExtra(EXTRA_SCAN_TIPS);
            if (scanInstructions != null) {
                overlay.setScanText(scanInstructions);
            }
        }
        overlay.setBorderColor(mScanRectColor);
        overlay.setScanBackGroundColor(mBackGroundColor);
        Bitmap horizontalScanLineBitmap = BitmapFactory.decodeResource(getResources(), MyResource.getIdByName(this,"mipmap","icon_scan_line"));
        overlay.setScanLineHorizontalBitmap(horizontalScanLineBitmap);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap verticalScanLineBitmap = Bitmap.createBitmap(horizontalScanLineBitmap, 0, 0,
                horizontalScanLineBitmap.getWidth(), horizontalScanLineBitmap.getHeight(), matrix, false);
        overlay.setScanLineVerticalBitmap(verticalScanLineBitmap);
        overlay.setScanOrientation(mCardOrientationVertical ? OverlayView.SCAN_ORIENTATION_VERTICAL :
                OverlayView.SCAN_ORIENTATION_HORIZONTAL);
        overlay.switchScan(mIsStartScanLine);
        return overlay;
    }

    /**
     * 是否开启扫描光标
     *
     * @param isStartScan
     */
    protected void switchScanStatus(boolean isStartScan) {
        OverlayView overlayView = getOverlayView();
        if (overlayView != null) {
            overlayView.switchScan(isStartScan);
        }
    }

    /**
     * 获取取景框的Rect
     *
     * @return 扫描框的Rect
     */
    protected Rect getCardScanFrame() {
        return mCardScanRect;
    }

    /**
     * @return
     */
    public int getFrameOrientation() {
        return mFrameOrientation;
    }

    public void onTextUpdate(final String text, final int textColor) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mOverlay instanceof OverlayView) {
                    ((OverlayView) mOverlay).updateTextAndColor(text, textColor);
                }
            }
        });
    }

    protected void resumeScanning() {
        if (mCardScanner != null) {
            mCardScanner.resumeScanning(mPlytPreview.getSurfaceHolder());
        }
    }

    protected void pauseScanning() {
        mCardScanner.pauseScanning();
    }

    private OverlayView getOverlayView() {
        OverlayView overlayView = null;
        if (mOverlay instanceof OverlayView) {
            overlayView = (OverlayView) mOverlay;
        }
        return overlayView;
    }

    @Override
    public void onCardDetected(Card card, Bitmap cameraApertureBitmap, Bitmap cropCardBitmap) {
        playVibrator();
        mCardScanner.pauseScanning();

        Intent dataIntent = new Intent();

        //设置带边框的图片
        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_CARD_IMAGE, false)) {
            ByteArrayOutputStream scaledCardBytes = new ByteArrayOutputStream();
            cameraApertureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytes);
            setReturnResult(EXTRA_CARD_IMAGE, scaledCardBytes.toByteArray());
        }

        //返回裁剪图
        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_CARD_IMAGE_RECTIFIED, false)
                && cropCardBitmap != null) {
            ByteArrayOutputStream scaledCardBytesRectified = new ByteArrayOutputStream();
            cropCardBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytesRectified);
            setReturnResult(EXTRA_CARD_IMAGE_RECTIFIED, scaledCardBytesRectified.toByteArray());
        }

        setReturnResult(EXTRA_SCAN_RESULT, card);

        setResultAndFinish(RESULT_CARD_INFO, dataIntent);
    }

    protected void setReturnResult(String key, Object value) {
        LFIntentTransportData.getInstance().putData(key, value);
    }

    /**
     * 播放震动
     */
    protected void playVibrator() {
        if (mCardPresenter != null) {
            mCardPresenter.playVibrator(this);
        }
    }

    /**
     * 设置识别的方向
     */
    protected boolean getCardOrientationVertical() {
        return mCardOrientationVertical;
    }

    /**
     * 设置识别的方向
     *
     * @param cardOrientationVertical
     */
    protected void setCardOrientationVertical(boolean cardOrientationVertical) {
        mCardOrientationVertical = cardOrientationVertical;
        CardScanner cardScanner = getCardScanner();
        if (cardScanner != null) {
            cardScanner.setCardOrientationVertical(mCardOrientationVertical);
        }
    }


    protected CardScanner getCardScanner() {
        return mCardScanner;
    }

    protected abstract CardScanner initCardScanner(Context context, int currentFrameOrientation, boolean isVertical);

    void setResultAndFinish(final int resultCode, final Intent data) {
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        LFLog.i(TAG, "landscapeTest", "surfaceCreated");
        initOverlayView();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        LFLog.i(TAG, "landscapeTest", "surfaceChanged");
        clearOverlayView();
        initOverlayView();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        LFLog.i(TAG, "landscapeTest", "surfaceDestroyed");
        clearOverlayView();
    }

    @Override
    public void initFail() {
        setResultAndFinish(RESULT_RECOGNIZER_INIT_FAILED, null);
    }

    @Override
    public void scanTimeOut() {
        setResultAndFinish(RESULT_RECOGNIZER_FAIL_SCAN_TIME_OUT, null);

    }
}
