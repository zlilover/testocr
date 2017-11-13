package com.sc.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by lizhen on 2017/11/7. 银行卡OCR
 */

public class IDCardOCRPlugin extends CordovaPlugin {
    private Context context;
    private CallbackContext callbackContext;

    /**
     * 扫描身份证正反面请求码
     */
    private static final int LF_SCAN_ID_CARD_BOTH_REQUEST = 102;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = cordova.getActivity();
        this.callbackContext = callbackContext;
        if ("startScanIDcard".equals(action)) {
            Intent intent = getScanBothIdCardIntent(IDCardRecognizer.Mode.FRONT, "请将身份证正面放入扫描框内");
            intent.setAction("ocrplugin.action.scan");
            cordova.startActivityForResult(this,intent,LF_SCAN_ID_CARD_BOTH_REQUEST);
            return true;
        }
        return super.execute(action, args, callbackContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        dealScanIDCardBothResult();
    }

    private Object getReturnResult(String key) {
        Object data = LFIntentTransportData.getInstance().getData(key);
        LFIntentTransportData.getInstance().removeData(key);
        return data;
    }

    /**
     * 处理扫描身份证正反面结果
     *
     */
    private void dealScanIDCardBothResult() {
        byte[] cropFrontImage = (byte[]) getReturnResult(IDCardBothActivity.KEY_FRONT_CROP_BITMAP);
        byte[] cropBackImage = (byte[]) getReturnResult(IDCardBothActivity.KEY_BACK_CROP_BITMAP);
        String frontIDCardBase64 = null;
        String backIDCardBase64 = null;
        if (cropFrontImage != null) {
            Bitmap mCameraApertureFrontBitmap = BitmapFactory.decodeByteArray(cropFrontImage, 0, cropFrontImage.length);
            frontIDCardBase64 = Util.bitmaptoString(mCameraApertureFrontBitmap,500);
        }

        if (cropBackImage != null) {
            Bitmap mCameraApertureBackBitmap = BitmapFactory.decodeByteArray(cropBackImage, 0, cropBackImage.length);
            backIDCardBase64 = Util.bitmaptoString(mCameraApertureBackBitmap,500);
        }
        String callBackMessage = frontIDCardBase64 + "||||" + backIDCardBase64;
        callbackContext.success(callBackMessage);
    }


    /**
     * 获取扫描超时时间
     *
     * @return int
     */
    private int getScanTimeOut() {
        return LFSpUtils.getScanTimeOut(context, 30);
    }

    private int getScanOrientation() {
        int cardScanOrientation = CardActivity.ORIENTATION_PORTRAIT;
        int settingScanOrientation = LFSpUtils.getScanOrientation(context, 0);
        switch (settingScanOrientation) {
            case LFSettingUtils.LF_SCAN_ORIENTATION_PORTRAIT:
                cardScanOrientation = CardActivity.ORIENTATION_PORTRAIT;
                break;
            case LFSettingUtils.LF_SCAN_ORIENTATION_LANDSCAPE_LEFT:
                cardScanOrientation = CardActivity.ORIENTATION_LANDSCAPE_LEFT;
                break;
            case LFSettingUtils.LF_SCAN_ORIENTATION_LANDSCAPE_RIGHT:
                cardScanOrientation = CardActivity.ORIENTATION_LANDSCAPE_RIGHT;
                break;
            default:
                cardScanOrientation = CardActivity.ORIENTATION_PORTRAIT;
                break;
        }
        return cardScanOrientation;
    }

    /**
     * 跳转IDCard正反面扫描界面
     *
     * @param mode IDCard扫描类型
     */
    private Intent getScanBothIdCardIntent(IDCardRecognizer.Mode mode, String scanText) {
        Intent scanIntent = new Intent();
        scanIntent.putExtra(IDCardBothActivity.EXTRA_BACK_DRAWABLE_ID, MyResource.getIdByName(context,"mipmap","icon_scan_back"));

        //设置身份证扫描类型
        scanIntent.putExtra(IDCardBothActivity.EXTRA_RECOGNIZE_MODE, mode);
        //设置身份证扫描文字
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_TIPS, scanText);

        // 设置扫描界面方向为竖直，设备头部朝上
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_ORIENTATION, getScanOrientation());
        // 设置需要返回身份证正面裁剪后的身份证图像
        scanIntent.putExtra(IDCardBothActivity.KEY_FRONT_CROP_BITMAP, true);
        // 设置需要返回身份证正面取景框的身份证图像
        scanIntent.putExtra(IDCardBothActivity.KEY_FRONT_CAMERA_APERTURE_BITMAP, true);
        // 设置需要返回身份证反面裁剪后的身份证图像
        scanIntent.putExtra(IDCardBothActivity.KEY_BACK_CROP_BITMAP, true);
        // 设置需要返回身份证反面取景框的身份证图像
        scanIntent.putExtra(IDCardBothActivity.KEY_BACK_CAMERA_APERTURE_BITMAP, true);
        //设置是否开启扫描光标
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_LINE_STATUS, LFSpUtils.getIsShowScanCursor(context));
        //扫描取景框边界颜色
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_GUIDE_COLOR, Color.parseColor("#78FFFFFF"));
        //是否需要在框内才能扫描，默认true
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_IS_IN_FRAME, getScanIsInFrame());
        //设置扫描的超时时间
        scanIntent.putExtra(IDCardBothActivity.EXTRA_SCAN_TIME_OUT, getScanTimeOut());
        return scanIntent;
    }

    private boolean getScanIsInFrame() {
        return LFSpUtils.getScanIsInFrame(context, true);
    }
}
