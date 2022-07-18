package cn.baby.love.common.manager;

import android.content.Intent;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.utils.ToastUtil;

/**
 * 登陆基类
 */
public class BaseLoginActivity extends BaseActivity {

    public SHARE_MEDIA share_media;
    /**
     * 三方登录授权,SHARE_MEDIA.WEIXIN
     *
     * @param share_media
     */
    public void authorization(SHARE_MEDIA share_media) {
        this.share_media = share_media;
        UMShareAPI.get(this).getPlatformInfo(this, share_media, umAuthListener);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            //ToastUtil.showToast(getApplicationContext(), "授权开始");
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            try {
                String uid = map.get("uid");//对应：qq:openid,微信:unionid，新浪:id；
                String name = map.get("name");
                String iconUrl = map.get("iconurl");
                String gender = map.get("gender");//男女
                onThirdLoginSuccess(uid,iconUrl,name,gender);
            } catch (Exception e) {
                ToastUtil.showToast(BaseLoginActivity.this, "登录错误！");
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//            ToastUtil.showToast(getApplicationContext(),"失败："+ throwable.getMessage());
            try {
                String errorMessage = throwable.getMessage();
                if (errorMessage != null && errorMessage.indexOf("2008") > -1) {
                    ToastUtil.showToast(getApplicationContext(), "未安装客户端,请先下载安装");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "授权失败");
                }
            } catch (Exception e) {
                ToastUtil.showToast(getApplicationContext(), "授权失败");
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
//            ToastUtil.showToast(getApplicationContext(), "取消");
        }
    };

    private final int REQUEST_CODE = 2001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == 200) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 三方登陆成功,通过此方法通知子类
     */
    public void onThirdLoginSuccess(String uid,String iconUrl,String name,String gender){

    }

}
