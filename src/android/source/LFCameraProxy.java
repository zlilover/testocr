package com.sc.plugin;


import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFCameraProxy {
    private static final String TAG = "LFCameraProxy";

    private static final int CAMERA_CONNECT_TIMEOUT = 200;
    private static final int CAMERA_CONNECT_RETRY_INTERVAL = 50;

    private Context mContext;
    /**
     * 相机
     */
    private Camera mCamera = null;

    /**
     * 开启前置后者后置的标志
     */
    private int mCameraId;

    /**
     * 相机方向
     */
    private int mCameraInfoOrientation;

    /**
     * 当前activity的扫描方向
     */
    private int mFrameOrientation;

    /**
     * 当前activity的旋转角度
     */
    protected int mRotation;

    /**
     * 相机预览宽高
     */
    int mPreviewWidth = 1280;
    int mPreviewHeight = 960;

    public LFCameraProxy(Context context) {
        this.mContext = context;
    }

    /**
     * 开启相机预览
     */
    public void openCamera() {
        if (mCamera == null) {
            mCamera = connectToCamera(CAMERA_CONNECT_RETRY_INTERVAL, CAMERA_CONNECT_TIMEOUT);
            if (mCamera != null) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCameraId, info);
                mCameraInfoOrientation = info.orientation;
                initCameraParameters();
            }
        }
    }

    /**
     * 初始化相机参数
     */
    private void initCameraParameters() {
        LFLog.i(TAG, "initCameraParameters", "rotation", mRotation);
        Util.setCameraDisplayOrientation(mRotation, mCameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        Camera.Size previewSize;
        previewSize = findBestPreviewSize();
        mPreviewWidth = previewSize.width;
        mPreviewHeight = previewSize.height;
        LFLog.i(TAG, "mPreviewWidth", mPreviewWidth, "mPreviewHeight", mPreviewHeight);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(parameters);
    }

    /**
     * 找出最佳的相机预览大小
     *
     * @return
     */
    private Camera.Size findBestPreviewSize() {
        List<Camera.Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution;
        if (mFrameOrientation == CardActivity.ORIENTATION_PORTRAIT) {
            screenResolution = new Point(display.getHeight(), display.getWidth());
        } else {
            screenResolution = new Point(display.getWidth(), display.getHeight());
        }
        Camera.Size previewSize = null;
        if (supportedPreviewSizes != null && supportedPreviewSizes.size() > 0) {
            double bestDis = Double.MAX_VALUE;
            for (Camera.Size size : supportedPreviewSizes) {
                // TODO-Alen we encounter a problem on Nexus 9(Android 5.0.1)
                // with the best preview size 1640x1230, the image is broken.
                // So the following conditions is a temporary fix, and shall be
                // replaced by a better solution.
                if (size.width == 1640) {
                    continue;
                }
                double w_h_dis, size_dis;
                w_h_dis = 1.0 + Math.abs(1.0
                        - ((double) (size.width * screenResolution.y)) / (double) (size.height * screenResolution.x));
                size_dis = 1.0 + Math.abs(1.0 - (size.width) / 1920.0);
                double newDis = w_h_dis * size_dis;
                if (newDis < bestDis) {
                    bestDis = newDis;
                    previewSize = size;
                }
            }
        }
        return previewSize;
    }

    /**
     * 连接相机
     *
     * @param checkInterval
     * @param maxTimeout
     * @return
     */
    private Camera connectToCamera(int checkInterval, int maxTimeout) {
        long start = System.currentTimeMillis();
        do {
            try {
                int numberOfCameras = Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mCameraId = i;
                        return Camera.open(mCameraId);
                    }
                }
            } catch (RuntimeException e) {
                try {
                    Log.w(Util.PUBLIC_LOG_TAG, String.format("暂时无法启用摄像头，等待%d毫秒后重试...", checkInterval));
                    Thread.sleep(checkInterval);
                } catch (InterruptedException e1) {
                    Log.e(Util.PUBLIC_LOG_TAG, "等待启用摄像头过程中出现异常", e1);
                }
            } catch (Exception e) {
                Log.e(Util.PUBLIC_LOG_TAG, "发生了未知错误，请与我们联系 https://www.linkface.cn", e);
                maxTimeout = 0;
            }

        } while (System.currentTimeMillis() - start < maxTimeout);
        return null;
    }

    /**
     * 开启相机预览
     */
    public void startPreview() {
        try {
            if (mCamera != null) {
                mCamera.startPreview();
            }
        } catch (RuntimeException e) {
            Log.e(Util.PUBLIC_LOG_TAG, "startPreview error:", e);
        }
    }

    /**
     * 停止相机预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void addCallbackBuffer(byte[] callbackBuffer) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(callbackBuffer);
        }
    }

    /**
     * 添加相机预览回调
     *
     * @param previewCallback
     */
    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback) {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
        }
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                Log.e(Util.PUBLIC_LOG_TAG, "stopPreview error:", e);
            }

            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置相机展示的容器
     *
     * @param holder
     */
    public void setPreviewDisplay(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * it does not get the camera permission by checkPermisson... function on VIVO Y67 device
     * so we do this to judge the permission result
     *
     * @param camera
     * @return
     */
    public boolean hasCameraPermission(Camera camera) {
        try {
            Field hasPermission = camera.getClass().getDeclaredField("mHasPermission");
            hasPermission.setAccessible(true);
            return (Boolean) hasPermission.get(camera);
        } catch (Exception e) {
            return true;
        }
    }

    public void setFrameOrientation(int frameOrientation) {
        this.mFrameOrientation = frameOrientation;
    }

    /**
     * 获取相机对象
     *
     * @return
     */
    public Camera getCamera() {
        return mCamera;
    }

    /**
     * 获取相机预览方向
     *
     * @return
     */
    public int getCameraInfoOrientation() {
        return mCameraInfoOrientation;
    }

    /**
     * 获取预览的宽
     *
     * @return
     */
    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    /**
     * 获取预览的高
     *
     * @return
     */
    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    public void setRotation(int rotation) {
        this.mRotation = rotation;
    }
}
