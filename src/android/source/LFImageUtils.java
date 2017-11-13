package com.sc.plugin;


import android.graphics.Bitmap;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFImageUtils {
    /**
     * @return 矫正并裁剪好的身份证图片
     */
    public static Bitmap getBitmap(int[] imageData, int bitmapWidth, int bitmapHeight) {
        Bitmap image = null;
        if (imageData != null) {
            image = Bitmap.createBitmap(imageData, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        }
        return image;
    }

    private static byte[] sRotateResult = null;

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = getStaticRotateResult(imageWidth, imageHeight);
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }

        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = getStaticRotateResult(imageWidth, imageHeight);
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = getStaticRotateResult(imageWidth, imageHeight);
        // Rotate the Y luma
        int i = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }// Rotate the U and V color components
        i = imageWidth * imageHeight;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
            }
        }
        return yuv;
    }

    private static byte[] getStaticRotateResult(int width, int height) {
        if (sRotateResult == null || sRotateResult.length != width * height * 3) {
            sRotateResult = new byte[width * height * 3 ];
        }
        return sRotateResult;
    }

    public static byte[] cropYUV420(byte[] data, int imageW, int imageH, int newImageH) {
        int cropH;
        int i, j, count, tmp;
        byte[] yuv = new byte[imageW * newImageH * 3 / 2];

        cropH = (imageH - newImageH) / 2;

        count = 0;
        for (j = cropH; j < cropH + newImageH; j++) {
            for (i = 0; i < imageW; i++) {
                yuv[count++] = data[j * imageW + i];
            }
        }

        //Cr Cb
        tmp = imageH + cropH / 2;
        for (j = tmp; j < tmp + newImageH / 2; j++) {
            for (i = 0; i < imageW; i++) {
                yuv[count++] = data[j * imageW + i];
            }
        }

        return yuv;
    }
}
