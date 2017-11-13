package com.sc.plugin;

import android.graphics.Rect;
import android.os.Parcel;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class IDCardViewData implements Serializable {

    private static final String TAG = IDCardViewData.class.getSimpleName();

    /**
     * 身份证正面或背面
     */
    public static enum Side {
        /**
         * 身份证正面
         */
        FRONT,
        /**
         * 身份证背面
         */
        BACK,
    }

    static final int IDCARD_Num_Mask = (1 << 0);
    static final int IDCARD_Sex_Mask = (1 << 1);
    static final int IDCARD_Birthday_Mask = (1 << 2);
    static final int IDCARD_Area_Mask = (1 << 3);

    static Side side;

    // 身份证正面内容
    // 文本
    String strName; // 姓名
    String strSex; // 性别
    String strNation; // 民族
    String strYear; // 出生年
    String strMonth; // 出生月
    String strDay; // 出生日
    String strAddress; // 住址
    String strID; // 公民身份证号

    // 身份证背面内容
    // 文本
    String strAuthority; // 签发机关
    String strValidity; // 有效期

    //区域图片位置
    int[] keyowrdRects = new int[8 * 4];
    int[] valueRects = new int[8 * 4];

    int validity;

    /**
     * IDCardOffLine 构造函数
     */
    public IDCardViewData() {
        reset();
    }

    private void reset() {
        side = null;
        strName = null;
        strSex = null;
        strNation = null;
        strYear = null;
        strMonth = null;
        strDay = null;
        strAddress = null;
        strID = null;
        strAuthority = null;
        strValidity = null;
        keyowrdRects = new int[8 * 4];
        valueRects = new int[8 * 4];

        validity = 0;
    }

    // parcelable
    private IDCardViewData(Parcel src) {
        side = (Side) src.readSerializable();
        strName = src.readString();
        strSex = src.readString();
        strNation = src.readString();
        strYear = src.readString();
        strMonth = src.readString();
        strDay = src.readString();
        strAddress = src.readString();
        strID = src.readString();
        strAuthority = src.readString();
        strValidity = src.readString();
        keyowrdRects = src.createIntArray();
        valueRects = src.createIntArray();

        validity = src.readInt();

    }

    /**
     * @return 正面所有文本识别结果
     */
    public String getFrontalInfo() {
        String strResultFull = "";

        strResultFull += "姓名: " + this.getStrName() + "\n";
        strResultFull += "性别: " + this.getStrSex() + "\n";
        strResultFull += "民族: " + this.getStrNation() + "\n";
        strResultFull += "出生: " + this.getStrDate() + "\n";
        strResultFull += "住址: " + this.getStrAddress() + "\n";
        strResultFull += "号码: " + this.getStrID() + "\n";

        return strResultFull;
    }

    /**
     * @return 背面所有文本识别结果
     */
    public String getBackSideInfo() {
        String strResultFull = "";

        strResultFull += "签发机关: " + this.getStrAuthority() + "\n";
        strResultFull += "有效期限: " + this.getStrValidity() + "\n";

        return strResultFull;
    }

    public Side getSide() {
        return side;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrSex() {
        return strSex;
    }

    public String getStrNation() {
        return strNation;
    }

    public String getStrYear() {
        return strYear;
    }

    public String getStrMonth() {
        if (TextUtils.isEmpty(strMonth))
            return strMonth;
        if (strMonth.length() == 1)
            return "0" + strMonth;
        return strMonth;
    }

    public String getStrDay() {
        if (TextUtils.isEmpty(strDay))
            return strDay;
        if (strDay.length() == 1)
            return "0" + strDay;
        return strDay;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public String getStrID() {
        return strID;
    }

    public String getStrDate() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStrYear())
                .append("-")
                .append(getStrMonth())
                .append("-")
                .append(getStrDay())
                .append("");
        return sb.toString();
    }

    public String getStrAuthority() {
        return strAuthority;
    }

    public String getStrValidity() {
        return strValidity;
    }

    private Rect getRectImage(int rectIndex) {
        Rect rect = new Rect();
        rect.left = valueRects[rectIndex * 4];
        rect.top = valueRects[rectIndex * 4 + 1];
        rect.right = valueRects[rectIndex * 4 + 2];
        rect.bottom = valueRects[rectIndex * 4 + 3];
        return rect;
    }

    public Rect getImgName() {
        return getRectImage(0);
    }

    public Rect getImgID() {
        return getRectImage(7);
    }

    public boolean isSexValid() {
        return (validity & IDCARD_Sex_Mask) != 0;
    }

    public void setSide(Side side) {
        IDCardViewData.side = side;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public void setStrSex(String strSex) {
        this.strSex = strSex;
    }

    public void setStrNation(String strNation) {
        this.strNation = strNation;
    }

    public void setStrYear(String strYear) {
        this.strYear = strYear;
    }

    public void setStrMonth(String strMonth) {
        this.strMonth = strMonth;
    }

    public void setStrDay(String strDay) {
        this.strDay = strDay;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }

    public void setStrAuthority(String strAuthority) {
        this.strAuthority = strAuthority;
    }

    public void setStrValidity(String strValidity) {
        this.strValidity = strValidity;
    }

    public void setKeyowrdRects(int[] keyowrdRects) {
        this.keyowrdRects = keyowrdRects;
    }

    public void setValueRects(int[] valueRects) {
        this.valueRects = valueRects;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "IDCardViewData{" +
                "strName='" + strName + '\'' +
                ", strSex='" + strSex + '\'' +
                ", strNation='" + strNation + '\'' +
                ", strYear='" + strYear + '\'' +
                ", strMonth='" + strMonth + '\'' +
                ", strDay='" + strDay + '\'' +
                ", strAddress='" + strAddress + '\'' +
                ", strID='" + strID + '\'' +
                ", strAuthority='" + strAuthority + '\'' +
                ", strValidity='" + strValidity + '\'' +
                ", keyowrdRects=" + Arrays.toString(keyowrdRects) +
                ", valueRects=" + Arrays.toString(valueRects) +
                ", validity=" + validity +
                '}';
    }
}
