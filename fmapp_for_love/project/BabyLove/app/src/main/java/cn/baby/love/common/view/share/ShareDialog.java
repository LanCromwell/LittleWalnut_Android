package cn.baby.love.common.view.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lzy.okgo.model.Response;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.bean.InvateInfo;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.dialog.CustomDialog;

/**
 * Created by wangguangbin on 2019/1/11.
 */

public class ShareDialog {

    private Activity mContext;
    private ShareBean mShareBean;
    private Dialog dialog;

    private ShareDialog(Activity activity) {
        this.mContext = activity;
    }

    public static ShareDialog getInstance(Activity activity) {
        return new ShareDialog(activity);
    }

    public Dialog showShareDialog(ShareBean mShareBean) {
        this.mShareBean = mShareBean;
        int screenWidth = ScreenUtil.getScreenWidth(mContext);
        dialog = CustomDialog.builder(mContext, R.layout.dialog_share);
        RelativeLayout mLinearLayout = dialog.findViewById(R.id.whiteBgView);
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        params.width = screenWidth - ScreenUtil.dip2px(mContext, 68 * 2);
        params.height = params.width * 335 / 240;
        mLinearLayout.setLayoutParams(params);

        dialog.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        dialog.findViewById(R.id.weixinLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAction(SHARE_MEDIA.WEIXIN);
            }
        });
        dialog.findViewById(R.id.pengyouquanLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAction(SHARE_MEDIA.WEIXIN_CIRCLE);
            }
        });
        dialog.findViewById(R.id.qqLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAction(SHARE_MEDIA.QZONE);
            }
        });

        return dialog;
    }

    private void shareAction(SHARE_MEDIA type) {
        UserBean userBean = UserUtil.getUserInfo();
        InvateInfo invateInfo = UserUtil.getUserInfo().invite_info;

        String shareUrl = invateInfo.href;


        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.share_data_load_fail));
            cancel();
            return;
        }
        ShareAction shareAction = new ShareAction(mContext);
        shareAction.setPlatform(type);

        UMWeb web = new UMWeb(shareUrl);
        web.setTitle(TextUtils.isEmpty(invateInfo.title) ? "小核桃" : invateInfo.title);//标题
        web.setThumb(new UMImage(mContext, R.drawable.ic_launcher_for_share));  //缩略图
        web.setDescription(TextUtils.isEmpty(invateInfo.description) ? "小核桃0-6岁孕育语音助手" : invateInfo.description);//描述

        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                //开始分享
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                ToastUtil.showToast(mContext, R.string.share_success);
                shareSuccess(mContext);
                cancel();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                ToastUtil.showToast(mContext, mContext.getString(R.string.share_error));
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtil.showToast(mContext, R.string.cancel);
                cancel();
            }
        });
        shareAction.withMedia(web).share();
    }


    private void cancel() {
        if (null != dialog && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    private static void shareSuccess(Context context){
        Api.getInstance().addDays(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                context.sendBroadcast(new Intent(BaseConfig.ACTION_NOTIFY_SHARE_SUCCESS));
            }
        });
    }

}
