package com.sc.plugin;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFIdCardInfo {
    /**
     *身份证各个区域的信息
     */
    private LFIdCardRegionInfo name;
    private LFIdCardRegionInfo sex;
    private LFIdCardRegionInfo nation;
    private LFIdCardRegionInfo year;
    private LFIdCardRegionInfo month;
    private LFIdCardRegionInfo day;
    private LFIdCardRegionInfo address;
    private LFIdCardRegionInfo idNum;
    private LFIdCardRegionInfo authority;
    private LFIdCardRegionInfo validity;

    @JSONField(name = "name")
    public LFIdCardRegionInfo getName() {
        return name;
    }

    @JSONField(name = "name")
    public void setName(LFIdCardRegionInfo name) {
        this.name = name;
    }

    @JSONField(name = "sex")
    public LFIdCardRegionInfo getSex() {
        return sex;
    }

    @JSONField(name = "sex")
    public void setSex(LFIdCardRegionInfo sex) {
        this.sex = sex;
    }

    @JSONField(name = "nation")
    public LFIdCardRegionInfo getNation() {
        return nation;
    }

    @JSONField(name = "nation")
    public void setNation(LFIdCardRegionInfo nation) {
        this.nation = nation;
    }

    @JSONField(name = "year")
    public LFIdCardRegionInfo getYear() {
        return year;
    }

    @JSONField(name = "year")
    public void setYear(LFIdCardRegionInfo year) {
        this.year = year;
    }

    @JSONField(name = "month")
    public LFIdCardRegionInfo getMonth() {
        return month;
    }

    @JSONField(name = "month")
    public void setMonth(LFIdCardRegionInfo month) {
        this.month = month;
    }

    @JSONField(name = "day")
    public LFIdCardRegionInfo getDay() {
        return day;
    }

    @JSONField(name = "day")
    public void setDay(LFIdCardRegionInfo day) {
        this.day = day;
    }

    @JSONField(name = "address")
    public LFIdCardRegionInfo getAddress() {
        return address;
    }

    @JSONField(name = "address")
    public void setAddress(LFIdCardRegionInfo address) {
        this.address = address;
    }

    @JSONField(name = "idNum")
    public LFIdCardRegionInfo getIdNum() {
        return idNum;
    }

    @JSONField(name = "idNum")
    public void setIdNum(LFIdCardRegionInfo idNum) {
        this.idNum = idNum;
    }

    @JSONField(name = "authority")
    public LFIdCardRegionInfo getAuthority() {
        return authority;
    }

    @JSONField(name = "authority")
    public void setAuthority(LFIdCardRegionInfo authority) {
        this.authority = authority;
    }

    @JSONField(name = "validity")
    public LFIdCardRegionInfo getValidity() {
        return validity;
    }

    @JSONField(name = "validity")
    public void setValidity(LFIdCardRegionInfo validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "LFIdCardInfo{" +
                "name=" + name +
                ", sex=" + sex +
                ", nation=" + nation +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", address=" + address +
                ", idNum=" + idNum +
                ", authority=" + authority +
                ", validity=" + validity +
                '}';
    }
}
