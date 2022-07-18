package cn.baby.love.common.view.swipebacklayout.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import cn.baby.love.common.view.swipebacklayout.SwipeBackLayout;
import cn.baby.love.common.view.swipebacklayout.SystemBarTintManager;
import cn.baby.love.common.view.swipebacklayout.Utils;

/**
 * Created by KathLine on 2016/7/19.
 * 可以右滑关闭Activity
 */
public abstract class SwipeBackActivity extends AppCompatActivity implements SwipeBackActivityBase, SwipeBackLayout.FinishActivityListener {
    private SwipeBackActivityHelper mHelper;
    @ColorInt
    private int mStatusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        setStateBarColor(this);
        mHelper.onPostCreate();
        getSwipeBackLayout().setOnFinishActivityListener(this);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public void dothingBeforeFinish() {

    }

    public void setStateBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 设置状态栏颜色
        ViewGroup contentLayout = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
        SystemBarTintManager.SystemBarConfig config = systemBarTintManager.getConfig();
        int actionBarHeight = config.getActionBarHeight();
        contentLayout.getChildAt(0).setPadding(0, getStatusBarHeight(activity) + actionBarHeight, 0, 0);
        if (mStatusBarColor == 0) {
            setupStatusBarView(activity, contentLayout, Color.parseColor("#ffffff"));
        }else {
            setupStatusBarView(activity, contentLayout, mStatusBarColor);
        }
        // 设置Activity layout的fitsSystemWindows
        View contentChild = contentLayout.getChildAt(0);
        contentChild.setFitsSystemWindows(true);//等同于在根布局设置android:fitsSystemWindows="true"
    }

    /**设置系统状态栏的颜色*/
    public void setStatusBarColor(@ColorInt int colorId, OnStatusBarColorListener listener) {
        mStatusBarColor = colorId;
        onStatusBarColorListener = listener;
    }

    private void setupStatusBarView(Activity activity, ViewGroup contentLayout, int color) {
        View statusBarView = new View(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        contentLayout.addView(statusBarView, lp);
        statusBarView.setBackgroundColor(color);
        if(onStatusBarColorListener != null){
            onStatusBarColorListener.onFinish();
        }
    }

    /**
     * 获得状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private OnStatusBarColorListener onStatusBarColorListener;

    /**
     * 设置状态栏背景色后，在回调方法中设置字体颜色
     */
    public interface OnStatusBarColorListener {
        void onFinish();
    }
}
