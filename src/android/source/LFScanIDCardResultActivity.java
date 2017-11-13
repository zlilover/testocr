package com.sc.plugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.linkface.ocr.idcard.IDCard;

import io.cordova.hellocordova.R;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 * IDCard扫描结果展示界面
 */

public class LFScanIDCardResultActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    public static final String TAG = "LFScanIDCardResultActivity";

    public static final String KEY_CARD_FRONT_DATA = "key_card_front_data";
    public static final String KEY_CARD_BACK_DATA = "key_card_back_data";
    public static final String KEY_CARD_RESULT_TYPE = "key_card_result_type";
    public static final String KEY_CARD_RESULT_TITLE = "key_card_result_title";
    public static final String KEY_CAMERA_APERTURE_FRONT_IMAGE = "key_camera_aperture_front_image";
    public static final String KEY_CAMERA_APERTURE_BACK_IMAGE = "key_camera_aperture_back_image";
    public static final String KEY_CROP_FRONT_IMAGE = "key_crop_front_image";
    public static final String KEY_CROP_BACK_IMAGE = "key_crop_back_image";

    public static final int CARD_RESULT_TYPE_FRONT = 1;
    public static final int CARD_RESULT_TYPE_BACK = 2;
    public static final int CARD_RESULT_TYPE_BOTH = 3;
    public static final int CARD_RESULT_TYPE_FRONT_QUICK = 4;

    private TextView mTvTitle;

    private ImageView mIvBack;

    private RadioGroup mRGSwitch;

    private RadioButton mRBFront;

    private RadioButton mRBBack;

    /**
     * 扫描身份证前面界面
     */
    private LFFrontCardResultView mVFrontCardResult;

    /**
     * 扫描身份证反面界面
     */
    private LFBackCardResultView mVBackCardResult;

//    /**
//     * 快速扫描身份证前面界面
//     */
//    private LFFrontQuickCardResultView mVFrontQuickResult;

    /**
     * 身份证正面扫描数据
     */
    private IDCardViewData mFrontCardViewData;

    /**
     * 身份证反面扫描数据
     */
    private IDCardViewData mBackCardViewData;

    /**
     * 身份证扫描类型
     */
    private int mCardResultType;

    /**
     * 标题
     */
    private String mTitle;

    /**
     * 正面取景框图像
     */
    private Bitmap mCameraApertureFrontBitmap;

    /**
     * 反面取景框图像
     */
    private Bitmap mCameraApertureBackBitmap;

    /**
     * 正面裁剪图
     */
    private Bitmap mCropFrontBitmap;

    /**
     * 反面裁剪图
     */
    private Bitmap mCropBackBitmap;

    private LFCardResultPresenter mCardPresenter;
    private IDCard mFrontIDCard;
    private IDCard mBackIDCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MyResource.getIdByName(this,"layout","layout_scan_id_card_result_main"));

        initPresenter();
        getIntentData();
        initView();
        initCardResultType(null, null);
        initData();
    }

    private void initPresenter() {
        mCardPresenter = new LFCardResultPresenter();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mCardResultType = intent.getIntExtra(KEY_CARD_RESULT_TYPE, CARD_RESULT_TYPE_FRONT);
            mTitle = intent.getStringExtra(KEY_CARD_RESULT_TITLE);
            byte[] cameraApertureFront = (byte[]) getCardByKey(KEY_CAMERA_APERTURE_FRONT_IMAGE);
            if (cameraApertureFront != null) {
                mCameraApertureFrontBitmap = BitmapFactory.decodeByteArray(cameraApertureFront, 0, cameraApertureFront.length);
//                Log.e("action","to base64...");
//                String frontIDCardBase64 = Util.bitmaptoString(mCameraApertureFrontBitmap,500);
//
//                Log.e("action","finish to base64,result is" + frontIDCardBase64);
//                Log.e("action","base64 to bitmap...");
//                mCameraApertureFrontBitmap = Util.base64ToBitmap(frontIDCardBase64);
            }

            byte[] cameraApertureBack = (byte[]) getCardByKey(KEY_CAMERA_APERTURE_BACK_IMAGE);
            if (cameraApertureBack != null) {
                mCameraApertureBackBitmap = BitmapFactory.decodeByteArray(cameraApertureBack, 0, cameraApertureBack.length);
            }

            byte[] cropFrontByte = (byte[]) getCardByKey(KEY_CROP_FRONT_IMAGE);
            if (cropFrontByte != null) {
                mCropFrontBitmap = BitmapFactory.decodeByteArray(cropFrontByte, 0, cropFrontByte.length);
            }

            byte[] cropBackByte = (byte[]) getCardByKey(KEY_CROP_BACK_IMAGE);
            if (cropBackByte != null) {
                mCropBackBitmap = BitmapFactory.decodeByteArray(cropBackByte, 0, cropBackByte.length);
            }

            mFrontIDCard = (IDCard) getCardByKey(KEY_CARD_FRONT_DATA);
            mBackIDCard = (IDCard) getCardByKey(KEY_CARD_BACK_DATA);
        }
    }

    private Object getCardByKey(String key) {
        Object result = LFIntentTransportData.getInstance().getData(key);
        LFIntentTransportData.getInstance().removeData(key);
        return result;
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(MyResource.getIdByName(this,"id","id_tv_title"));
        mIvBack = (ImageView) findViewById(MyResource.getIdByName(this,"id","id_iv_back"));
        mRGSwitch = (RadioGroup) findViewById(MyResource.getIdByName(this,"id","id_rg_scan_result_switch"));
        mRBFront = (RadioButton) findViewById(MyResource.getIdByName(this,"id","id_rb_scan_front_result"));
        mRBBack = (RadioButton) findViewById(MyResource.getIdByName(this,"id","id_rb_scan_back_result"));
        mVFrontCardResult = (LFFrontCardResultView) findViewById(MyResource.getIdByName(this,"id","id_lcrv_front"));
        mVBackCardResult = (LFBackCardResultView) findViewById(MyResource.getIdByName(this,"id","id_lcrv_back"));
//        mVFrontQuickResult = (LFFrontQuickCardResultView) findViewById(R.id.id_lcrv_front_quick);
        TextView tvComplete = (TextView) findViewById(MyResource.getIdByName(this,"id","id_tv_complete"));

        mRGSwitch.setOnCheckedChangeListener(this);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTvTitle.setText(mTitle);
    }

    private void initCardResultType(IDCardViewData frontCardViewData, IDCardViewData backCardViewData) {
        switch (mCardResultType) {
            case CARD_RESULT_TYPE_FRONT:
                refreshFrontView(frontCardViewData);
                break;
            case CARD_RESULT_TYPE_BACK:
                refreshBackView(backCardViewData);
                break;
            case CARD_RESULT_TYPE_FRONT_QUICK:
                refreshFrontQuickView(frontCardViewData);
                break;
            case CARD_RESULT_TYPE_BOTH:
                refreshBothView(frontCardViewData, backCardViewData);
                break;
            default:
                break;
        }
    }

    private void refreshFrontView(IDCardViewData frontCardViewData) {
        mRGSwitch.setVisibility(View.GONE);
        mRBFront.setChecked(true);
        boolean isCanEdit = LFSpUtils.getScanContentIsCanEdit(this);
        mVFrontCardResult.refreshData(frontCardViewData, isCanEdit, mCameraApertureFrontBitmap, mCropFrontBitmap);
    }

    private void refreshBackView(IDCardViewData backCardViewData) {
        mRGSwitch.setVisibility(View.GONE);
        mRBBack.setChecked(true);
        boolean isCanEdit = LFSpUtils.getScanContentIsCanEdit(this);
        mVBackCardResult.refreshData(backCardViewData, isCanEdit, mCameraApertureBackBitmap, mCropBackBitmap);
    }

    private void refreshFrontQuickView(IDCardViewData frontCardViewData) {
        mRGSwitch.setVisibility(View.GONE);
//        mVFrontQuickResult.setVisibility(View.VISIBLE);
        boolean isCanEdit = LFSpUtils.getScanContentIsCanEdit(this);
//        mVFrontQuickResult.refreshData(frontCardViewData, isCanEdit, mCameraApertureBackBitmap, mCropBackBitmap);
    }

    private void refreshBothView(IDCardViewData frontCardViewData, IDCardViewData backCardViewData) {
        mRGSwitch.setVisibility(View.VISIBLE);
        mRBFront.setChecked(true);
        boolean isCanEdit = LFSpUtils.getScanContentIsCanEdit(this);
        mVFrontCardResult.refreshData(frontCardViewData, isCanEdit, mCameraApertureFrontBitmap, mCropFrontBitmap);
        mVBackCardResult.refreshData(backCardViewData, isCanEdit, mCameraApertureBackBitmap, mCropBackBitmap);
    }

    private void initData() {
        if (mFrontIDCard != null) {
            mCardPresenter.getCardViewData(mFrontIDCard, new LFCardResultPresenter.ICardResultCallback() {
                @Override
                public void callback(IDCardViewData frontCardViewData) {
                    mFrontCardViewData = frontCardViewData;
                    if (mBackIDCard != null) {
                        mCardPresenter.getCardViewData(mBackIDCard, new LFCardResultPresenter.ICardResultCallback() {
                            @Override
                            public void callback(IDCardViewData frontCardViewData) {
                                mBackCardViewData = frontCardViewData;
                                refreshData();
                            }

                            @Override
                            public void fail(String error) {
                                showToast("解析数据失败");
                                finish();
                            }
                        });
                    } else {
                        refreshData();
                    }
                }

                @Override
                public void fail(String error) {
                    showToast("解析数据失败");
                    finish();
                }
            });
        } else {
            if (mBackIDCard != null) {
                mCardPresenter.getCardViewData(mBackIDCard, new LFCardResultPresenter.ICardResultCallback() {
                    @Override
                    public void callback(IDCardViewData frontCardViewData) {
                        mBackCardViewData = frontCardViewData;
                        refreshData();
                    }

                    @Override
                    public void fail(String error) {
                        showToast("解析数据失败");
                        finish();
                    }
                });
            }
        }

    }

    private void refreshData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initCardResultType(mFrontCardViewData, mBackCardViewData);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        int id_rb_scan_front_result = MyResource.getIdByName(this,"id","id_rb_scan_front_result");
        int id_rb_scan_back_result = MyResource.getIdByName(this,"id","id_rb_scan_back_result");
        if (checkedRadioButtonId == id_rb_scan_front_result) {
            refreshCardResultView(true);
        } else if (checkedRadioButtonId == id_rb_scan_back_result) {
            refreshCardResultView(false);

        }
//        switch (checkedRadioButtonId) {
//            case id_rb_scan_front_result:
//                refreshCardResultView(true);
//                break;
//            case id_rb_scan_back_result:
//                refreshCardResultView(false);
//                break;
//        }
    }

    private void refreshCardResultView(boolean showFrontView) {
        mVFrontCardResult.setVisibility(showFrontView ? View.VISIBLE : View.GONE);
        mVBackCardResult.setVisibility(!showFrontView ? View.VISIBLE : View.GONE);
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LFScanIDCardResultActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
