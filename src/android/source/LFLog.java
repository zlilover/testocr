package com.sc.plugin;


import android.text.TextUtils;
import android.util.Log;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFLog {
    private static boolean sIsDebug = true;
    private static String STAG = "lf_log";

    public static final int LOG_MESSAGE_MAX_LENGTH = 100;

    public static void init(boolean isDebug, String logTag) {
        sIsDebug = isDebug;
        if (TextUtils.isEmpty(logTag)) {
            STAG = logTag;
        }
    }

    private static boolean isCanLog() {
        return sIsDebug;
    }

    public static void d(Object... message) {
        if (isCanLog()) {
            Log.d(STAG, getLogcat(message));
        }
    }

    @Deprecated
    public static void d(int... message) {
        if (isCanLog()) {
            Log.d(STAG, getLogMessage(String.valueOf(message)));
        }
    }

    public static void i(Object... message) {
        if (isCanLog()) {
            Log.i(STAG, getLogcat(message));
        }
    }

    @Deprecated
    public static void i(int message) {
        if (isCanLog()) {
            Log.i(STAG, getLogMessage(String.valueOf(message)));
        }
    }

    public static void w(Object... message) {
        if (isCanLog()) {
            Log.w(STAG, getLogcat(message));
        }
    }

    @Deprecated
    public static void w(int message) {
        if (isCanLog()) {
            Log.w(STAG, getLogMessage(String.valueOf(message)));
        }
    }

    public static void e(Object... message) {
        if (isCanLog()) {
            Log.e(STAG, getLogcat(message));
        }
    }

    @Deprecated
    public static void e(int message) {
        if (isCanLog()) {
            Log.e(STAG, getLogMessage(String.valueOf(message)));
        }
    }

    public static String getLogcat(Object... message) {
        StringBuilder sb = new StringBuilder();
        if (message != null) {
            for (Object object : message) {
                sb.append("*******");
                sb.append(object);
            }
        }
        return sb.toString();
    }

    private static String getPartString(StringBuilder sb) {
        String result = "";
        if (sb != null) {
            if (sb.length() > LOG_MESSAGE_MAX_LENGTH) {
                StringBuilder partSb = new StringBuilder();
                int index = 0;
                while (sb.length() > LOG_MESSAGE_MAX_LENGTH * index) {
                    int subEndIndex = 0;
                    if (LOG_MESSAGE_MAX_LENGTH * (index + 1) > sb.length()) {
                        subEndIndex = sb.length();
                    } else {
                        subEndIndex = LOG_MESSAGE_MAX_LENGTH * (index + 1);
                    }
                    partSb.append(sb.substring(LOG_MESSAGE_MAX_LENGTH * index, subEndIndex));
                    index++;
                }
                result = partSb.toString();
            } else {
                result = sb.toString();
            }
        }
        return result;
    }

    public static String getLogMessage(String message) {
        return "time**".concat("**").concat(message);
    }
}
