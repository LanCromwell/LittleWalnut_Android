package cn.baby.love.common.utils;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * Created by wangguangbin on 2018/11/6.
 * 全局crash
 */
public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private static CrashHandlerUtil mCrashHandler = new CrashHandlerUtil();
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandlerUtil getInstance() {
       return mCrashHandler;
    }

    private CrashHandlerUtil() {
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (handleException(e)) { // 已经人为处理,系统自己退出
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } else { // 未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(t, e);
            }
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        new Thread() {// 在主线程中弹出提示
            @Override
            public void run() {
                Looper.prepare();
                //TODO code....
                //Toast.makeText(mContext, "捕获到异常", Toast.LENGTH_SHORT).show();
                Log.e("handleException", "__捕获到异常");
                Looper.loop();
            }
        }.start();
        return false;
    }
}
