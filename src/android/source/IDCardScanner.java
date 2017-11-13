package com.sc.plugin;


import android.content.Context;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 * <p>
 * IDard扫描类
 */
class IDCardScanner extends CardScanner {
    protected IDCardScanner(Context context, int currentFrameOrientation) {
        super(context, currentFrameOrientation, false);
    }

    /**
     * 设置IDCard扫描的识别模式参考IDCardRecognizer.Mode
     *
     * @param mode
     */
    public void setRecognizerMode(final IDCardRecognizer.Mode mode) {
        IDCardRecognizer idCardRecognizer = getIDCardRecognizer();
        if (idCardRecognizer != null) {
            idCardRecognizer.setMode(mode);
        }
    }

    /**
     * 设置需要识别的的身份证字段Mask
     *
     * @param flag
     */
    public void setRecognizerFlag(final int flag) {
        IDCardRecognizer idCardRecognizer = getIDCardRecognizer();
        if (idCardRecognizer != null) {
            idCardRecognizer.setRecognizeFlag(flag);
        }
    }

    @Override
    protected CardRecognizer initRecognizer(Context context) throws RecognizerInitFailException {
        return new IDCardRecognizer(context);
    }

    private IDCardRecognizer getIDCardRecognizer() {
        IDCardRecognizer idCardRecognizer = null;
        CardRecognizer cardRecognizer = getCardRecognizer();
        if (cardRecognizer instanceof IDCardRecognizer) {
            idCardRecognizer = (IDCardRecognizer) cardRecognizer;
        }
        return idCardRecognizer;
    }

}
