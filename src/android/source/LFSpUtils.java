package com.sc.plugin;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */

public class LFSpUtils {
    public static final String LF_SP_ATTENDANCE = "lf_sp_ocr";

    public static final String LF_SHOW_SCAN_CURSOR = "lf_show_scan_cursor";
    public static final String LF_SCAN_CONTENT_CAN_EDIT = "lf_scan_content_can_edit";
    public static final String LF_SCAN_ORIENTATION = "lf_scan_orientation";
    public static final String LF_SCAN_IS_IN_FRAME = "lf_scan_is_in_frame";
    public static final String LF_SCAN_IS_START_TIME_OUT = "lf_scan_is_start_time_out";
    public static final String LF_SCAN_TIME_OUT = "lf_scan_time_out";


    public static boolean getIsShowScanCursor(Context context) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getBoolean(sharedPreferences, LF_SHOW_SCAN_CURSOR, true);
    }

    public static void saveIsShowScanCursor(Context context, boolean isShowScanCursor) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveBoolean(sharedPreferences, LF_SHOW_SCAN_CURSOR, isShowScanCursor);
    }

    public static boolean getScanContentIsCanEdit(Context context) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getBoolean(sharedPreferences, LF_SCAN_CONTENT_CAN_EDIT, true);
    }

    public static void saveScanContentIsCanEdit(Context context, boolean isCanEdit) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveBoolean(sharedPreferences, LF_SCAN_CONTENT_CAN_EDIT, isCanEdit);
    }

    public static void saveScanOrientation(Context context, int scanOrientation) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveInt(sharedPreferences, LF_SCAN_ORIENTATION, scanOrientation);
    }

    public static int getScanOrientation(Context context, int defaultValue) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getInt(sharedPreferences, LF_SCAN_ORIENTATION, defaultValue);
    }

    public static void saveScanIsInFrame(Context context, boolean isInFrame) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveBoolean(sharedPreferences, LF_SCAN_IS_IN_FRAME, isInFrame);
    }

    public static boolean getScanIsInFrame(Context context, boolean defaultValue) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getBoolean(sharedPreferences, LF_SCAN_IS_IN_FRAME, defaultValue);
    }

    public static void saveScanIsStartTimeOut(Context context, boolean isStartTimeOut) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveBoolean(sharedPreferences, LF_SCAN_IS_START_TIME_OUT, isStartTimeOut);
    }

    public static boolean getScanIsStartTimeOut(Context context, boolean defaultValue) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getBoolean(sharedPreferences, LF_SCAN_IS_START_TIME_OUT, defaultValue);
    }

    public static void saveScanTimeOut(Context context, int scanTimeOut) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        saveInt(sharedPreferences, LF_SCAN_TIME_OUT, scanTimeOut);
    }

    public static int getScanTimeOut(Context context, int defaultValue) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        return getInt(sharedPreferences, LF_SCAN_TIME_OUT, defaultValue);
    }

    public static void removeScanTimeOut(Context context) {
        SharedPreferences sharedPreferences = null;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(LF_SP_ATTENDANCE, Activity.MODE_PRIVATE);
        }
        remove(sharedPreferences, LF_SCAN_TIME_OUT);
    }

    public static void saveBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(key, value)
                    .commit();
        }
    }

    public static boolean getBoolean(SharedPreferences sharedPreferences, String key, boolean defaultValue) {
        boolean result = defaultValue;
        if (sharedPreferences != null) {
            result = sharedPreferences.getBoolean(key, defaultValue);
        }
        return result;
    }

    public static void saveInt(SharedPreferences sharedPreferences, String key, int value) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(key, value)
                    .commit();
        }
    }

    public static int getInt(SharedPreferences sharedPreferences, String key, int defaultValue) {
        int result = defaultValue;
        if (sharedPreferences != null) {
            result = sharedPreferences.getInt(key, defaultValue);
        }
        return result;
    }

    private static void remove(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(key)
                    .commit();
        }
    }

}
