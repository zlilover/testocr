package com.sc.plugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.linkface.ocr.card.Card;
import com.linkface.ocr.idcard.IDCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class IDCardBothActivity extends IDCardActivity {

    private static final String TAG = IDCardActivity.class.getSimpleName();

    /**
     * 身份证前面裁剪图
     */
    public static final String KEY_FRONT_CROP_BITMAP = "key_front_crop_bitmap";

    /**
     * 身份证前面裁剪框的图
     */
    public static final String KEY_FRONT_CAMERA_APERTURE_BITMAP = "key_front_camera_aperture_bitmap";

    /**
     * 身份证反面裁剪图
     */
    public static final String KEY_BACK_CROP_BITMAP = "key_back_crop_bitmap";

    /**
     * 身份证反面裁剪框的图
     */
    public static final String KEY_BACK_CAMERA_APERTURE_BITMAP = "key_back_camera_aperture_bitmap";

    /**
     * 身份证正面数据
     */
    public static final String KEY_FRONT_CARD_DATA = "key_front_card_data";

    /**
     * 身份证反面数据
     */
    public static final String KEY_BACK_CARD_DATA = "key_back_card_data";

    private Intent mIntent = null;

    private Card mFrontIDCard;

    boolean isFront = true;

    // 重写这个方法来自定义检测到卡片后的处理方式
    @Override
    public void onCardDetected(Card card, Bitmap cameraApertureBitmap, Bitmap cropCardBitmap) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        // 处理过程中先暂停扫描
        pauseScanning();
        // 获取识别出的是哪一面
        IDCard idCard = null;
        if (card instanceof IDCard) {
            idCard = (IDCard) card;
        }
        if (idCard != null) {
            if (isFront) {
                isFront = false;
                onTextUpdate("扫描成功", getResources().getColor(MyResource.getIdByName(this,"color","green")));

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        onTextUpdate("请将身份证反面放入扫描框内", Color.WHITE);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000);//“扫描成功”显示1秒

                // 返回取景框图像
                if (getIntent() != null && getIntent().getBooleanExtra(KEY_FRONT_CAMERA_APERTURE_BITMAP, false)) {
                    ByteArrayOutputStream scaledCardBytes = new ByteArrayOutputStream();
                    cameraApertureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytes);
                    LFIntentTransportData.getInstance().putData(KEY_FRONT_CAMERA_APERTURE_BITMAP, scaledCardBytes.toByteArray());
                    try {
                        scaledCardBytes.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                //返回裁剪图
                if (getIntent() != null && getIntent().getBooleanExtra(KEY_FRONT_CROP_BITMAP, false) && cropCardBitmap != null) {
                    ByteArrayOutputStream scaledCardBytesRectified = new ByteArrayOutputStream();
                    cropCardBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytesRectified);
                    LFIntentTransportData.getInstance().putData(KEY_FRONT_CROP_BITMAP, scaledCardBytesRectified.toByteArray());
                    try {
                        scaledCardBytesRectified.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                mFrontIDCard = card;

                // 设置仅识别背面
                setRecognizeMode(IDCardRecognizer.Mode.BACK);
                // 继续扫描
                resumeScanning();

            } else if (!isFront) {
                // 扫描完背面，返回数据或进行下一步操作

                // 返回原始图像
                if (getIntent() != null && getIntent().getBooleanExtra(KEY_BACK_CAMERA_APERTURE_BITMAP, false)) {
                    ByteArrayOutputStream scaledCardBytes = new ByteArrayOutputStream();
                    cameraApertureBitmap = resizeImage(cameraApertureBitmap, 1280, 800);
                    cameraApertureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytes);
                    LFIntentTransportData.getInstance().putData(KEY_BACK_CAMERA_APERTURE_BITMAP, scaledCardBytes.toByteArray());
                    try {
                        scaledCardBytes.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // 返回裁剪图像
                if (getIntent() != null && getIntent().getBooleanExtra(KEY_BACK_CROP_BITMAP, false) && cropCardBitmap != null) {
                    ByteArrayOutputStream scaledCardBytesRectified = new ByteArrayOutputStream();
                    cropCardBitmap.compress(Bitmap.CompressFormat.JPEG, 80, scaledCardBytesRectified);
                    LFIntentTransportData.getInstance().putData(KEY_BACK_CROP_BITMAP, scaledCardBytesRectified.toByteArray());
                    try {
                        scaledCardBytesRectified.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                LFIntentTransportData.getInstance().putData(KEY_FRONT_CARD_DATA, mFrontIDCard);
                LFIntentTransportData.getInstance().putData(KEY_BACK_CARD_DATA, card);

                playVibrator();
                setResult(RESULT_CARD_INFO, mIntent);
                finish();
            }
        }
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

}

