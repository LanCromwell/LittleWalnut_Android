package cn.baby.love.common.utils;

import android.app.Activity;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by wangguangbin on 2018/11/6.
 * 友盟工具类
 * https://developer.umeng.com/docs/66632/detail/66639#h2-u624Bu52A8u96C6u62106
 */

public class UmentUtils {

    /**
     * 打开分享面板
     * @param context
     */
    public static void openSharePanel(Activity context){
        new ShareAction(context).withText("hello")
                .setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN)
                .setCallback(shareListener).open();
    }

    /**
     * 打开分享无面板
     * @param activity
     * @param mSHARE_MEDIA 分享平台 SHARE_MEDIA.QQ
     */
    public static void openShareNoPanel(Activity activity,SHARE_MEDIA mSHARE_MEDIA){
        new ShareAction(activity)
                .setPlatform(SHARE_MEDIA.QQ)//传入平台
                .withText("hello")//分享内容
                .setCallback(shareListener)//回调监听器
                .share();
    }

    private static UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(ShareTestActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(ShareTestActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(ShareTestActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

}
