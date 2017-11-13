package com.sc.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.cordova.hellocordova.R;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFBackCardResultView extends LinearLayout {
    /**
     * 裁剪图
     */
    private ImageView mIvCrop;
    /**
     * 取景框图片
     */
    private ImageView mIvCameraAperture;

    /**
     * IDCard内容
     */
    private LinearLayout mCardContent;
    private Context context;

    public LFBackCardResultView(Context context) {
        super(context);
        init(context);
    }

    public LFBackCardResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LFBackCardResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context != null) {
            View frontCardView = inflate(context, MyResource.getIdByName(context,"layout","layout_view_front_card_result"), null);
            addView(frontCardView);
            initView();
        }
    }

    private void initView() {
        mIvCrop = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_crop"));
        mIvCameraAperture = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_camera_aperture"));
        ImageView ivFace = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_face_image"));
        mCardContent = (LinearLayout) findViewById(MyResource.getIdByName(context,"id","id_llyt_card_content"));

        ivFace.setVisibility(View.GONE);
    }

    public void refreshData(IDCardViewData cardViewData, boolean isCanEdit, Bitmap cameraApertureBitmap, Bitmap cropBitmap) {
        if (mIvCameraAperture != null) {
            mIvCameraAperture.setImageBitmap(cameraApertureBitmap);
        }
        if (mIvCrop != null) {
            mIvCrop.setImageBitmap(cropBitmap);
        }
        initCardContent(cardViewData, isCanEdit);

    }

    private void initCardContent(IDCardViewData cardViewData, boolean isCanEdit) {

        if (mCardContent != null && cardViewData != null) {
            //签发机关
            View nameCardItemView = getCardItemContentView("签发机关", cardViewData.getStrAuthority(), isCanEdit);
            mCardContent.addView(nameCardItemView);
            //有效期限
            View sexCardItemView = getCardItemContentView("有效期限", cardViewData.getStrValidity(), isCanEdit);
            mCardContent.addView(sexCardItemView);
        }

    }

    private View getCardItemContentView(String title, String content, boolean isCanEdit) {
        View itemView = inflate(getContext(), MyResource.getIdByName(getContext(),"layout","layout_view_card_result_item"), null);
        TextView tvTitle = (TextView) itemView.findViewById(MyResource.getIdByName(getContext(),"id","id_tv_title"));
        TextView tvContent = (TextView) itemView.findViewById(MyResource.getIdByName(getContext(),"id","id_tv_content"));
        tvContent.setEnabled(isCanEdit);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        if (tvContent != null) {
            tvContent.setText(content);
        }
        return itemView;
    }
}
