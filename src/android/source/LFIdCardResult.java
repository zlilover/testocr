package com.sc.plugin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFIdCardResult {

    public static final int ID_CARD_SIDE_FRONT = 1;
    public static final int ID_CARD_SIDE_BACK = 2;

    /**
     *
     */
    private String status;
    private int valid;
    private int type;
    private int orient;
    private int side;
    private List<LFPoint> corners;
    private LFIdCardInfo info;
    private float qualityScore;
    private String imageCrop;
    private int code;
    private String version;
    private String requestId;

    @JSONField(name = "status")
    public String getStatus() {
        return status;
    }

    @JSONField(name = "status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JSONField(name = "valid")
    public int getValid() {
        return valid;
    }

    @JSONField(name = "valid")
    public void setValid(int valid) {
        this.valid = valid;
    }

    @JSONField(name = "type")
    public int getType() {
        return type;
    }

    @JSONField(name = "type")
    public void setType(int type) {
        this.type = type;
    }

    @JSONField(name = "orient")
    public int getOrient() {
        return orient;
    }

    @JSONField(name = "orient")
    public void setOrient(int orient) {
        this.orient = orient;
    }

    @JSONField(name = "side")
    public int getSide() {
        return side;
    }

    @JSONField(name = "side")
    public void setSide(int side) {
        this.side = side;
    }

    @JSONField(name = "corners")
    public List<LFPoint> getCorners() {
        return corners;
    }

    @JSONField(name = "corners")
    public void setCorners(List<LFPoint> corners) {
        this.corners = corners;
    }

    @JSONField(name = "info")
    public LFIdCardInfo getInfo() {
        return info;
    }

    @JSONField(name = "info")
    public void setInfo(LFIdCardInfo info) {
        this.info = info;
    }

    @JSONField(name = "code")
    public int getCode() {
        return code;
    }

    @JSONField(name = "code")
    public void setCode(int code) {
        this.code = code;
    }

    @JSONField(name = "version")
    public String getVersion() {
        return version;
    }

    @JSONField(name = "version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JSONField(name = "request_id")
    public String getRequestId() {
        return requestId;
    }

    @JSONField(name = "request_id")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JSONField(name = "quality_score")
    public float getQualityScore() {
        return qualityScore;
    }

    @JSONField(name = "quality_score")
    public void setQualityScore(float qualityScore) {
        this.qualityScore = qualityScore;
    }

    @JSONField(name = "image_crop")
    public String getImageCrop() {
        return imageCrop;
    }

    @JSONField(name = "image_crop")
    public void setImageCrop(String imageCrop) {
        this.imageCrop = imageCrop;
    }

    @Override
    public String toString() {
        return "LFIdCardResult{" +
                "status='" + status + '\'' +
                ", valid=" + valid +
                ", type=" + type +
                ", orient=" + orient +
                ", side=" + side +
                ", corners=" + corners +
                ", info=" + info +
                ", code=" + code +
                ", version='" + version + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
