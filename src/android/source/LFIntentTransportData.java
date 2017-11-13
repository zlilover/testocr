package com.sc.plugin;


import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFIntentTransportData {
    private Map<String, Object> mContain;

    private static LFIntentTransportData sInstance;

    private LFIntentTransportData() {
        mContain = new HashMap<String,Object>();
    }

    public static LFIntentTransportData getInstance() {
        if (sInstance == null) {
            synchronized (LFIntentTransportData.class) {
                if (sInstance == null) {
                    sInstance = new LFIntentTransportData();
                }
            }
        }
        return sInstance;
    }

    public void putData(String key, Object value) {
        if (mContain != null) {
            mContain.put(key, value);
        }
    }

    public Object getData(String key) {
        return mContain.get(key);
    }

    public Object removeData(String key) {
        return mContain.remove(key);
    }
}
