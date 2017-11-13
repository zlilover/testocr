package com.sc.plugin;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFPoint {
    private float x;
    private float y;

    @JSONField(name = "x")
    public float getX() {
        return x;
    }

    @JSONField(name = "x")
    public void setX(float x) {
        this.x = x;
    }

    @JSONField(name = "y")
    public float getY() {
        return y;
    }

    @JSONField(name = "y")
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "LFPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
