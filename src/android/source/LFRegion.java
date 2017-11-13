package com.sc.plugin;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFRegion {
    /**
     * 区域
     * "left": 95,
     * "top": 109,
     * "right": 199,
     * "bottom": 148
     */
    private int left;
    private int top;
    private int right;
    private int bottom;

    @JSONField(name = "left")
    public int getLeft() {
        return left;
    }

    @JSONField(name = "left")
    public void setLeft(int left) {
        this.left = left;
    }

    @JSONField(name = "top")
    public int getTop() {
        return top;
    }

    @JSONField(name = "top")
    public void setTop(int top) {
        this.top = top;
    }

    @JSONField(name = "right")
    public int getRight() {
        return right;
    }

    @JSONField(name = "right")
    public void setRight(int right) {
        this.right = right;
    }

    @JSONField(name = "bottom")
    public int getBottom() {
        return bottom;
    }

    @JSONField(name = "bottom")
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "LFRegion{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
