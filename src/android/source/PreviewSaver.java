package com.sc.plugin;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class PreviewSaver {
    private final static String TAG = "PreviewSaver";
    private int mPreviewTimeGaps = -1; // unit:second
    private String mPreviewStoragePath = null;
    private long mCurrentTime = 0;
    private long mLastTime = 0;
    private int mPreviewW;
    private int mPreviewH;
    private int mSavedNum; // the preview saved number

    private int mCurrentSaveNum = 0;

    public PreviewSaver(int gaps, String storagePath, int savedNum) {
        mPreviewTimeGaps = gaps;
        mPreviewStoragePath = storagePath;
        mSavedNum = savedNum;
    }

    public void setPreviewSize(int previewW, int previewH) {
        mPreviewH = previewH;
        mPreviewW = previewW;
    }

    public void saveBuffer(final Context context,final int rotateDegree, byte[] data) {
        if (mCurrentTime == 0) {
            mLastTime = mCurrentTime = System.currentTimeMillis();
        }

        mCurrentTime = System.currentTimeMillis();
        if (mCurrentTime - mLastTime >= mPreviewTimeGaps * 1000) {
            mLastTime = mCurrentTime;
            new AsyncTask<byte[], Void, Void>() {

                @Override
                protected Void doInBackground(byte[]... params) {
                    byte[] tempData = params[0];
                    if (tempData != null) {
                        Bitmap bitmap = Util.NV21ToRGBABitmap(tempData, mPreviewW, mPreviewH, rotateDegree, null, context);
                        if (bitmap != null) {
                            ByteArrayOutputStream jpegBuf = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, jpegBuf);
                            String filePath = mPreviewStoragePath + File.separatorChar + mCurrentSaveNum + ".jpg";
                            saveData(filePath, jpegBuf.toByteArray());
                            synchronized (PreviewSaver.class) {
                                mCurrentSaveNum++;
                                if (mCurrentSaveNum == mSavedNum) {
                                    mCurrentSaveNum = 0;
                                }
                            }
                            Log.e(TAG, "save success: " + mCurrentSaveNum + " w: " + mPreviewW + " h: " + mPreviewH);
                            try {
                                jpegBuf.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute(data);
        }
    }

    public static void saveData(String path, byte[] data) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }
            if (null != data) {
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(data);
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
