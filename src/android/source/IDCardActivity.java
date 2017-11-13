package com.sc.plugin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class IDCardActivity extends CardActivity {


    /**
     * 传入身份证识别模式
     * 类型为 {@link IDCardRecognizer.Mode}
     */
    public static final String EXTRA_RECOGNIZE_MODE = "com.linkface.idcard.recognizeMode";

    /**
     * 传入需要识别的的身份证字段Mask {@link IDCardRecognizer#RECOGNIZE_FLAG_ALL}(default)
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_NAME}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_SEX}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_NATION}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_BIRTH}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_ADDR}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_ID}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_AUTHORITY}
     * {@link IDCardRecognizer#RECOGNIZE_FLAG_VALIDITY}
     */
    public static final String EXTRA_RECOGNIZE_FLAG = "com.linkface.idcard.recognizeFlag";

    /**
     * 设置需要输出身份证人脸截图<br>
     * 输入类型为boolean，true表示要输出，false表示不需要输出<br>
     * 可以在{@link Activity#onActivityResult(int, int, Intent)}中使用<br>
     * data.getByteArrayExtra(CardActivity.EXTRA_IDCARD_FACE)获取JPEG字节数组
     */
    public static final String EXTRA_IDCARD_FACE = "com.linkface.idcard.face";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setRecognizeMode(IDCardRecognizer.Mode mode) {
        IDCardScanner idCardScanner = getIDCardScanner();
        if (idCardScanner != null) {
            idCardScanner.setRecognizerMode(mode);
        }
    }

    protected void setRecognizerFlag(int flag) {
        IDCardScanner idCardScanner = getIDCardScanner();
        if (idCardScanner != null) {
            idCardScanner.setRecognizerFlag(flag);
        }
    }

    /**
     * 初始化IDCard扫描类
     *
     * @param context
     * @param currentFrameOrientation
     * @param isVertical
     * @return
     */
    @Override
    protected CardScanner initCardScanner(Context context, int currentFrameOrientation, boolean isVertical) {
        IDCardScanner idCardScanner = new IDCardScanner(context, currentFrameOrientation);

        return idCardScanner;
    }

    @Override
    protected void createCardScan() {
        super.createCardScan();
        Intent intent = getIntent();
        if (intent != null) {
            IDCardRecognizer.Mode recognizeMode = (IDCardRecognizer.Mode) intent.getSerializableExtra(EXTRA_RECOGNIZE_MODE);
            int recognizeFlag = intent.getIntExtra(EXTRA_RECOGNIZE_FLAG, IDCardRecognizer.RECOGNIZE_FLAG_ALL);

            setRecognizeMode(recognizeMode);
            setRecognizerFlag(recognizeFlag);
        }
    }

    private IDCardScanner getIDCardScanner() {
        IDCardScanner idCardScanner = null;
        CardScanner cardScanner = getCardScanner();
        if (cardScanner instanceof IDCardScanner) {
            idCardScanner = (IDCardScanner) cardScanner;
        }
        return idCardScanner;
    }
}
