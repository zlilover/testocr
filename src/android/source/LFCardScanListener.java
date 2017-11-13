package com.sc.plugin;


import android.graphics.Bitmap;

import com.linkface.ocr.card.Card;


/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public interface LFCardScanListener {
    //OCR扫描初始化失败
    void initFail();

    void scanTimeOut();

    void onCardDetected(Card card, Bitmap cardBitmap, Bitmap rectifiedCardBitmap);
}
