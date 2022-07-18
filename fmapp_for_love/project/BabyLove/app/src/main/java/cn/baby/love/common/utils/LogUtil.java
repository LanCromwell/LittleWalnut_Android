package cn.baby.love.common.utils;

import android.util.Log;

import cn.baby.love.common.base.BaseApi;
import cn.baby.love.common.base.BaseConfig;

/**
 * Created by guangbinw on 2018/11/6.
 * Log统一管理类
 */
public class LogUtil {

    private static final String TAG = "baby";

    private static boolean isOut = true;

    static {
//        isOut = BaseConfig.isDebug;
        isOut = true;
    }

    public static void v(String msg) {
        if (isOut) Log.v(TAG, msg==null?"msg is null":msg);
    }

    public static void i(String msg) {
        if (isOut) Log.i(TAG, msg==null?"msg is null":msg);
    }

    public static void d(String msg) {
        if (isOut) Log.d(TAG, msg==null?"msg is null":msg);
    }

    public static void e(String msg) {
        if (isOut) Log.e(TAG, msg==null?"msg is null":msg);
    }

    public static void v(String tag, String msg) {
        if (isOut) Log.v(tag, msg==null?"msg is null":msg);
    }

    public static void i(String tag, String msg) {
        if (isOut) Log.i(tag, msg==null?"msg is null":msg);
    }

    public static void d(String tag, String msg) {
        if (isOut) Log.d(tag, msg==null?"msg is null":msg);
    }

    public static void e(String tag, String msg) {
        if (isOut) Log.e(tag, msg==null?"msg is null":msg);
    }

}
