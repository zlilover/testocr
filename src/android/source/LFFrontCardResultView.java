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

public class LFFrontCardResultView extends LinearLayout {
    /**
     * 裁剪图
     */
    private ImageView mIvCrop;
    /**
     * 取景框图片
     */
    private ImageView mIvCameraAperture;

    /**
     * 人脸图片
     */
    private ImageView mIvFace;

    /**
     * IDCard内容
     */
    private LinearLayout mCardContent;
    private Context context;

    public LFFrontCardResultView(Context context) {
        super(context);
        init(context);
    }

    public LFFrontCardResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LFFrontCardResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context != null) {
            this.context = context;
            View frontCardView = inflate(context, MyResource.getIdByName(context,"layout","layout_view_front_card_result"), null);
            addView(frontCardView);
            initView();
        }
    }

    private void initView() {
        mIvCrop = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_crop"));
        mIvCameraAperture = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_camera_aperture"));
        mIvFace = (ImageView) findViewById(MyResource.getIdByName(context,"id","id_iv_front_card_face_image"));
        mCardContent = (LinearLayout) findViewById(MyResource.getIdByName(context,"id","id_llyt_card_content"));
    }

    public void refreshData(IDCardViewData cardViewData, boolean isCanEdit, Bitmap cameraApertureBitmap, Bitmap cropBitmap) {
        if (mIvCameraAperture != null) {
            mIvCameraAperture.setImageBitmap(cameraApertureBitmap);
        }
        if (mIvCrop != null) {
            mIvCrop.setImageBitmap(cropBitmap);
        }

        if (mIvFace != null && cropBitmap != null) {
            int height = cropBitmap.getHeight();
            int width = cropBitmap.getWidth();
            Bitmap avatar = Bitmap.createBitmap(cropBitmap, width / 8 * 5, height / 7, width / 3, height / 5 * 3);
            mIvFace.setImageBitmap(avatar);

        }
        if (cardViewData != null) {
            initCardContent(cardViewData, isCanEdit);
        }

    }

    private void initCardContent(IDCardViewData cardViewData, boolean isCanEidt) {

        if (mCardContent != null) {
            //姓名
            View nameCardItemView = getCardItemContentView("姓名", cardViewData.getStrName(), isCanEidt);
            mCardContent.addView(nameCardItemView);
            //性别
            View sexCardItemView = getCardItemContentView("性别", cardViewData.getStrSex(), isCanEidt);
            mCardContent.addView(sexCardItemView);
            //民族
            View nationCardItemView = getCardItemContentView("民族", cardViewData.getStrNation(), isCanEidt);
            mCardContent.addView(nationCardItemView);
            //年龄
            View birthCardItemView = getCardItemContentView("出生", cardViewData.getStrDate(), isCanEidt);
            mCardContent.addView(birthCardItemView);
            //住址
            View addressCardItemView = getCardItemContentView("住址", cardViewData.getStrAddress(), isCanEidt);
            mCardContent.addView(addressCardItemView);
            //号码
            View numberCardItemView = getCardItemContentView("号码", cardViewData.getStrID(), isCanEidt);
            mCardContent.addView(numberCardItemView);
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
