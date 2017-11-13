package com.sc.plugin;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.linkface.ocr.idcard.IDCard;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFCardResultPresenter {
    private static final String TAG = "LFCardResultPresenter";

    private static String BASE_URL = "https://cloudapi.linkface.cn/ocr/parse_idcard_ocr_result";


//    #注意:请填写APP_ID和APP_SECRET
    private static String APP_ID = "8b7bcdf86d22487f8d270ae0ee0097f3";
    private static String APP_SECRET = "d04a9c54becc4439a957f9553adbacae";

    public LFCardResultPresenter() {
        Log.i(TAG, "LFCardResultPresenter" + "***online");
        LFHttpRequestUtils.initClient(BASE_URL);
    }

    public void getCardViewData(IDCard idCard, final ICardResultCallback cardResultCallback) {
        Log.i(TAG, "dealIDCardRecognizeResult" + "decodeCardResult");
        if (idCard != null) {
            LFHttpRequestUtils.postDecodeCard(APP_ID, APP_SECRET, idCard.getCardResult(),
                    new LFNetworkCallback() {

                        @Override
                        public void failed(int httpStatusCode, String error) {
                            super.failed(httpStatusCode, error);
                            Log.i(TAG, "dealIDCardRecognizeResult" + "error" + error);
                            if (TextUtils.isEmpty(error)) {
                                error = "解析失败";
                            }
                            if (cardResultCallback != null) {
                                cardResultCallback.fail(error);
                            }
                        }

                        @Override
                        public void completed(String response) {
                            Log.i(TAG, "dealIDCardRecognizeResult" + "response" + response);
                            if (!TextUtils.isEmpty(response)) {
                                LFIdCardResult cardResult = JSON.parseObject(response, LFIdCardResult.class);

                                IDCardViewData cardViewData = getIDCardViewData(cardResult);

                                if (cardResultCallback != null) {
                                    cardResultCallback.callback(cardViewData);
                                }

                            }
                        }
                    });
        } else {
            if (cardResultCallback != null) {
                cardResultCallback.fail("IDCard为空");
            }
        }
    }

    private IDCardViewData getIDCardViewData(LFIdCardResult cardResult) {
        IDCardViewData viewData = new IDCardViewData();
        if (cardResult != null) {
            LFIdCardInfo info = cardResult.getInfo();
            if (info != null) {
                LFIdCardRegionInfo nameRegion = info.getName();
                LFIdCardRegionInfo idNumRegion = info.getIdNum();

                viewData.setStrName(getRegionInfoText(nameRegion));
                viewData.setStrSex(getRegionInfoText(info.getSex()));
                viewData.setStrNation(getRegionInfoText(info.getNation()));
                viewData.setStrYear(getRegionInfoText(info.getYear()));
                viewData.setStrMonth(getRegionInfoText(info.getMonth()));
                viewData.setStrDay(getRegionInfoText(info.getDay()));
                viewData.setStrAddress(getRegionInfoText(info.getAddress()));
                viewData.setStrID(getRegionInfoText(idNumRegion));
                viewData.setStrAuthority(getRegionInfoText(info.getAuthority()));
                viewData.setStrValidity(getRegionInfoText(info.getValidity()));

                int[] valueRests = new int[8 * 4];
                valueRests = setValueRests(valueRests, nameRegion, 0);
                valueRests = setValueRests(valueRests, idNumRegion, 7);

                viewData.setValueRects(valueRests);

            }
            viewData.setValidity(cardResult.getValid());
            int side = cardResult.getSide();
            viewData.setSide(getRecognizeSide(side));
        }

        return viewData;
    }

    private IDCardViewData.Side getRecognizeSide(int type) {
        IDCardViewData.Side side = IDCardViewData.Side.FRONT;
        switch (type) {
            case LFIdCardResult.ID_CARD_SIDE_FRONT:
                side = IDCardViewData.Side.FRONT;
                break;
            case LFIdCardResult.ID_CARD_SIDE_BACK:
                side = IDCardViewData.Side.BACK;
                break;
            default:
                side = IDCardViewData.Side.FRONT;
                break;
        }
        return side;
    }

    private int[] setValueRests(int[] valueRest, LFIdCardRegionInfo regionInfo, int startPosition) {
        if (regionInfo != null) {
            LFRegion textRegion = regionInfo.getTextRegion();
            if (textRegion != null) {
                valueRest[startPosition * 4 + 0] = textRegion.getLeft();
                valueRest[startPosition * 4 + 1] = textRegion.getTop();
                valueRest[startPosition * 4 + 2] = textRegion.getRight();
                valueRest[startPosition * 4 + 3] = textRegion.getBottom();
            }
        }
        return valueRest;
    }

    private String getRegionInfoText(LFIdCardRegionInfo regionInfo) {
        String text = "";
        if (regionInfo != null) {
            text = regionInfo.getText();
        }
        return text;
    }

    public interface ICardResultCallback {
        void callback(IDCardViewData frontCardViewData);

        void fail(String error);
    }


}
