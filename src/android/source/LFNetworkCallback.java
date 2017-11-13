package com.sc.plugin;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public abstract class LFNetworkCallback {


    public abstract void completed(String response);

    /**
     * @param httpStatusCode
     * @param error
     */
    public void failed(int httpStatusCode, String error) {

    }
}