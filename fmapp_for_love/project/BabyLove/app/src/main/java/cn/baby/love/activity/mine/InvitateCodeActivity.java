package cn.baby.love.activity.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.bean.InvateInfo;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.CopyToClipboard;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.ToastUtil;

/**
 * 邀请码
 */
public class InvitateCodeActivity extends BaseActivity {

    @BindView(R.id.leftIcon)
    ImageView leftIcon;
    @BindView(R.id.centerTv)
    TextView centerTv;
    @BindView(R.id.rightTv1)
    TextView rightTv1;
    @BindView(R.id.rightTv2)
    TextView rightTv2;

    @BindView(R.id.invateCodeTv)
    TextView invateCodeTv;
    @BindView(R.id.copyTv)
    TextView copyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitate);
        ButterKnife.bind(this);
        initTitle("我的邀请码");
        leftIcon.setImageResource(R.drawable.icon_back_black);
        centerTv.setTextColor(getResources().getColor(R.color.color_31));

        try {
            UserBean userBean = UserUtil.getUserInfo();
            if(TextUtils.isEmpty(userBean.invite_info.invitation_code)){
                invateCodeTv.setText("");
            }else{
                invateCodeTv.setText(userBean.invite_info.invitation_code);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @OnClick({R.id.weixinLayout, R.id.pengyouquanLayout, R.id.qqLayout,R.id.copyTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.weixinLayout:
                clickShareType = SHARE_MEDIA.WEIXIN;
                checkPermissions();
                break;
            case R.id.pengyouquanLayout:
                clickShareType = SHARE_MEDIA.WEIXIN_CIRCLE;
                checkPermissions();
                break;
            case R.id.qqLayout:
                clickShareType = SHARE_MEDIA.QZONE;
                checkPermissions();
                break;
            case R.id.copyTv://复制
                CopyToClipboard.onClickCopyWithToast(InvitateCodeActivity.this,invateCodeTv.getText().toString(),"复制成功");
                break;
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions(){
        requestWritePermissions();
    }

    private SHARE_MEDIA clickShareType = SHARE_MEDIA.WEIXIN;

    @Override
    public void hasPermissions() {
        super.hasPermissions();

        shareAction(clickShareType);

    }

    private void shareAction(SHARE_MEDIA type) {

        InvateInfo invateInfo = UserUtil.getUserInfo().invite_info;

        String shareUrl = invateInfo.href;


        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast(InvitateCodeActivity.this, getString(R.string.share_data_load_fail));
            return;
        }
        ShareAction shareAction = new ShareAction(InvitateCodeActivity.this);
        shareAction.setPlatform(type);

        UMWeb web = new UMWeb(shareUrl);
        web.setTitle(TextUtils.isEmpty(invateInfo.title) ? "小核桃" : invateInfo.title);//标题
        web.setThumb(new UMImage(InvitateCodeActivity.this, R.drawable.ic_launcher));  //缩略图
        web.setDescription(TextUtils.isEmpty(invateInfo.description) ? "小核桃0-6岁孕育语音助手" : invateInfo.description);//描述

        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                //开始分享
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                ToastUtil.showToast(InvitateCodeActivity.this, R.string.share_success);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                ToastUtil.showToast(InvitateCodeActivity.this, getString(R.string.share_error));
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtil.showToast(InvitateCodeActivity.this, R.string.cancel);
            }
        });
        shareAction.withMedia(web).share();
    }


}
