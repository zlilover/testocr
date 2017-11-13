package com.sc.plugin;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFIdCardRegionInfo {
    /**
     * 身份证各区域信息
     * "valid": 1,
     * "keyword_region": ,
     * "text_region": ,
     * "text": 李三
     */
    private int valid;
    private LFRegion keywordRegion;
    private LFRegion textRegion;
    private String text;

    @JSONField(name = "valid")
    public int getValid() {
        return valid;
    }

    @JSONField(name = "valid")
    public void setValid(int valid) {
        this.valid = valid;
    }

    @JSONField(name = "keyword_region")
    public LFRegion getKeywordRegion() {
        return keywordRegion;
    }

    @JSONField(name = "keyword_region")
    public void setKeywordRegion(LFRegion keywordRegion) {
        this.keywordRegion = keywordRegion;
    }

    @JSONField(name = "text_region")
    public LFRegion getTextRegion() {
        return textRegion;
    }

    @JSONField(name = "text_region")
    public void setTextRegion(LFRegion textRegion) {
        this.textRegion = textRegion;
    }

    @JSONField(name = "text")
    public String getText() {
        return text;
    }

    @JSONField(name = "text")
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "LFIdCardRegionInfo{" +
                "valid=" + valid +
                ", keywordRegion=" + keywordRegion +
                ", textRegion=" + textRegion +
                ", text='" + text + '\'' +
                '}';
    }
}
