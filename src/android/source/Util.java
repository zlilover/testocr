package com.sc.plugin;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Base64;
import android.util.TimingLogger;
import android.view.Surface;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.Locale;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class Util {

    public static final String PUBLIC_LOG_TAG = "com.linkface.card";

    public static final String TIMING_LOG_TAG = "STTiming";

    private static final String TAG = Util.class.getSimpleName();

    public static void setupTextPaintStyle(Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        paint.setAntiAlias(true);
        float[] black = {0f, 0f, 0f};
        paint.setShadowLayer(1.5f, 0.5f, 0f, Color.HSVToColor(200, black));
    }

    private static RenderScript mRS = null;

    static ScriptIntrinsicYuvToRGB mYuvToRgb = null;

    static Allocation ain = null;

    static Allocation aOut = null;

    static Bitmap bitmap = null;

    static Bitmap sCropBitmap = null;

    static byte[] bitmapByte;

    @SuppressLint("NewApi")
    public static Bitmap NV21ToRGBABitmap(byte[] nv21, int width, int height,
                                          Context context) {

//        recycleBitmap(bitmap);

        TimingLogger timings = new TimingLogger(TIMING_LOG_TAG, "NV21ToRGBABitmap");

        Rect rect = new Rect(0, 0, width, height);
        LFLog.i(TAG, "NV21ToRGBABitmap", "nv21.length", nv21.length, "width", width, "height", height);
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        timings.addSplit("NV21 bytes to YuvImage");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(rect, 90, baos);
        bitmapByte = baos.toByteArray();
        timings.addSplit("YuvImage crop and compress to Jpeg Bytes");
        // already cropped
        rect.right = rect.right - rect.left;
        rect.left = 0;
        rect.bottom = rect.bottom - rect.top;
        rect.top = 0;

        bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        timings.addSplit("Jpeg Bytes to Bitmap");
        timings.dumpToLog();
        return bitmap;
    }

    @SuppressLint("NewApi")
    public static Bitmap NV21ToRGBABitmap(byte[] nv21, int width, int height, int rotateDegree, Rect cropRect,
                                          Context context) {

        recycleBitmap(bitmap);

        TimingLogger timings = new TimingLogger(TIMING_LOG_TAG, "NV21ToRGBABitmap");

        Rect rect = new Rect(0, 0, width, height);
        if (cropRect != null) {
            if (rotateDegree == 0 || rotateDegree == 180) {
                rect = new Rect(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom);
            } else if (rotateDegree == 90 || rotateDegree == 270) {
                rect = new Rect(cropRect.top, cropRect.left, cropRect.bottom, cropRect.right);
            }
        }

        if (!isCPUInfo64()) {

            try {
                Class.forName("android.renderscript.Element$DataKind").getField("PIXEL_YUV");
                Class.forName("android.renderscript.ScriptIntrinsicYuvToRGB");
                byte[] imageData = nv21;
                if (mRS == null) {
                    mRS = RenderScript.create(context);
                    mYuvToRgb = ScriptIntrinsicYuvToRGB.create(mRS, Element.U8_4(mRS));
                    Type.Builder tb = new Type.Builder(mRS,
                            Element.createPixel(mRS, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
                    tb.setX(width);
                    tb.setY(height);
                    tb.setMipmaps(false);
                    tb.setYuvFormat(ImageFormat.NV21);
                    ain = Allocation.createTyped(mRS, tb.create(), Allocation.USAGE_SCRIPT);
                    timings.addSplit("Prepare for ain");
                    Type.Builder tb2 = new Type.Builder(mRS, Element.RGBA_8888(mRS));
                    tb2.setX(width);
                    tb2.setY(height);
                    tb2.setMipmaps(false);
                    aOut = Allocation.createTyped(mRS, tb2.create(), Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
                    timings.addSplit("Prepare for aOut");
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    timings.addSplit("Create Bitmap");
                }
                ain.copyFrom(imageData);
                timings.addSplit("ain copyFrom");
                mYuvToRgb.setInput(ain);
                timings.addSplit("setInput ain");
                mYuvToRgb.forEach(aOut);
                timings.addSplit("NV21 to ARGB forEach");
                aOut.copyTo(bitmap);
                timings.addSplit("Allocation to Bitmap");
            } catch (Exception e) {
                YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
                timings.addSplit("NV21 bytes to YuvImage");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(rect, 90, baos);
                bitmapByte = baos.toByteArray();
                timings.addSplit("YuvImage crop and compress to Jpeg Bytes");
                // already cropped
                rect.right = rect.right - rect.left;
                rect.left = 0;
                rect.bottom = rect.bottom - rect.top;
                rect.top = 0;

                bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
                timings.addSplit("Jpeg Bytes to Bitmap");
            }
        } else {
            LFLog.i(TAG, "NV21ToRGBABitmap", "nv21.length", nv21.length, "width", width, "height", height);
            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            timings.addSplit("NV21 bytes to YuvImage");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(rect, 90, baos);
            bitmapByte = baos.toByteArray();
            timings.addSplit("YuvImage crop and compress to Jpeg Bytes");
            // already cropped
            rect.right = rect.right - rect.left;
            rect.left = 0;
            rect.bottom = rect.bottom - rect.top;
            rect.top = 0;

            bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            timings.addSplit("Jpeg Bytes to Bitmap");
        }

        if (rotateDegree >= 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateDegree);
            bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top,
                    Math.min(rect.width(), bitmap.getWidth() - rect.left),
                    Math.min(rect.height(), bitmap.getHeight() - rect.top), matrix, false);
            timings.addSplit("Bitmap rotate & crop");
        }

        timings.dumpToLog();
        return bitmap;
    }

    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        input = new ByteArrayInputStream(imgByte);
        SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     */
    private static boolean isCPUInfo64() {
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo != null && cpuInfo.exists()) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(cpuInfo);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 512);
                String line = bufferedReader.readLine();
                if (line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64")) {
                    return true;
                }
            } catch (Throwable t) {
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void setCameraDisplayOrientation(int rotation, int cameraId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 此方法描述的是：   unicode转化为中文
     *
     * @author: zli
     * @version: 2015-8-26 下午2:59:08
     */
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5)
                        && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
                        .charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(
                                unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    /**
     * bitmap转base64
     * @param bitmap
     * @param bitmapQuality 期望的文件大小边界 单位KB
     * @author: zli
     * @version: 2015-8-26 下午2:59:08
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap, int bitmapQuality) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > bitmapQuality) { // 循环判断如果压缩后图片是否大于bitmapQuality kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        byte[] bytes = baos.toByteArray();
        String imgBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        if (imgBase64 != null) {
            imgBase64 = imgBase64.replaceAll("\\s*|\t|\r|\n", "");
        }
        return Util.decode(imgBase64);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
