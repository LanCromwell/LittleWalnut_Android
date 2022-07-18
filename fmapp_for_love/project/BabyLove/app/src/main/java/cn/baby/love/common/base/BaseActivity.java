package cn.baby.love.common.base;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.manager.ActivityManager;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.dialog.LoadingDialog;
import cn.baby.love.common.view.swipebacklayout.app.SwipeBackActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * @author wangxin
 * @date 2018-11-6
 * @desc Activity基类
 */
public class BaseActivity extends SwipeBackActivity implements EasyPermissions.PermissionCallbacks {

    public RelativeLayout leftIcon;
    public ImageButton rightIcon;
    public TextView titleTv;
    public TextView rightTv1;
    public TextView rightTv2;
    private LoadingDialog loadingDialog;
    public Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.add(this);
        gson = new Gson();
        //设置状态栏背景色
        if(!supportTranslucent()){
            setStatusBarColor(getResources().getColor(R.color.white), new OnStatusBarColorListener() {
                @Override
                public void onFinish() {
                    //设置顶部状态栏字体颜色为黑色
                    QMUIStatusBarHelper.setStatusBarLightMode(BaseActivity.this);
                }
            });
        }
        //设置状态栏字体颜色
        QMUIStatusBarHelper.setStatusBarLightMode(BaseActivity.this);
        if(isSettingTranslucentStatusBar()){
            QMUIStatusBarHelper.translucent(this);
        }
        PushAgent.getInstance(this).onAppStart();//友盟消息推送

    }

    /**
     * 是否设置沉浸式状态栏
     * @return
     */
    public boolean isSettingTranslucentStatusBar(){
        return true;
    }

    private static boolean supportTranslucent() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                // Essential Phone 不支持沉浸式，否则系统又不从状态栏下方开始布局又给你下发 WindowInsets
                && !Build.BRAND.toLowerCase().contains("essential");
    }

    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

//    public void showGifLoading(){
//        if (null == loadingDialog) {
//            loadingDialog = LoadingDialog.getInstance(this, "");
//        }
//        if (!loadingDialog.isShowing()) {
//            loadingDialog.showGif();
//        }
//    }

    public void showLoading(String loadingMsg) {
        if (null == loadingDialog) {
            loadingDialog = LoadingDialog.getInstance(this, loadingMsg);
        }

        loadingDialog.setTipText(loadingMsg);
        if (!loadingDialog.isShowing()) {
            loadingDialog.showGif();
        }
    }

    public void cancelLoading() {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        StatService.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
        Api.getInstance().cancelCall(this);
    }


    public void initTitle(String titleStr) {
        leftIcon = (RelativeLayout) findViewById(R.id.leftIconLy);
        rightIcon = (ImageButton) findViewById(R.id.rightIcon);
        titleTv = (TextView) findViewById(R.id.centerTv);
        rightTv1 = (TextView) findViewById(R.id.rightTv1);
        rightTv2 = (TextView) findViewById(R.id.rightTv2);
        titleTv.setText(titleStr);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    final int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean isMainRequestpermission = false;

    public void mainActivityReqeusPermissions(){
        isMainRequestpermission = true;
        requestWritePermissions();
    }

    @AfterPermissionGranted(REQUEST_EXTERNAL_STORAGE)
    public void requestWritePermissions() {
        if (EasyPermissions.hasPermissions(this, PERMISSIONS_STORAGE)) {
            if(isMainRequestpermission){
                mainHasPermission();
            }else{
                hasPermissions();
            }
        } else {
            String text = "分享需要访问您设备上的照片和媒体内容";
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, REQUEST_EXTERNAL_STORAGE, PERMISSIONS_STORAGE)
                            .setRationale(text)
                            .setTheme(R.style.permisson_dialog_styke)
                            .build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(isMainRequestpermission){
            mainHasPermission();
        }else{
            hasPermissions();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //如果用户点击永远禁止，这个时候就需要跳到系统设置页面去手动打开了
//        String[] PERMISSIONS_STORAGE = {
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (!EasyPermissions.hasPermissions(this,PERMISSIONS_STORAGE)) {
//            String text = "分享需要访问您设备上的照片和媒体内容";
//            new AppSettingsDialog.Builder(this)
//                    .setRationale(text)
//                    .setThemeResId(R.style.permisson_dialog_styke)
//                    .setTitle("权限请求")
//                    .build().show();
//        }
    }

    //获得了权限
    public void hasPermissions() {

    }

    //主页面获取权限
    public void mainHasPermission() {
        isMainRequestpermission = false;
    }

}
