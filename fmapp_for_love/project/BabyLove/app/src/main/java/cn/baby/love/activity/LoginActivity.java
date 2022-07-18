package cn.baby.love.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;
import cn.baby.love.activity.mine.SettingsActivity;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.base.NetConfig;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.manager.ActivityManager;
import cn.baby.love.common.manager.BaseLoginActivity;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.PreferUtil;
import cn.baby.love.common.utils.StrUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.dialog.UserProtocolDialog;

/**
 * 登录页面
 */
public class LoginActivity extends BaseLoginActivity {

    @BindView(R.id.phoneNumTv)
    EditText phoneNumTv;
    @BindView(R.id.verfyCodeEt)
    EditText verfyCodeEt;
    @BindView(R.id.send_code)
    TextView sendCodeView;
    private Gson gson;

    //三方登陆用户信息
    private String third_uid;
    private String third_iconUrl;
    private String third_name;
    private String third_gender;
    private int third_type;//三方登陆方式 0-手机密码   1-微信   2-qq

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        gson = new Gson();
        ActivityManager.clearOthers(this);

        //是否同意用户协议
        boolean isAgree = (boolean) PreferUtil.getInstance(LoginActivity.this).getObject(PreferUtil.KEY_IS_AGREE_XIEYI, false);
        if (!isAgree) {
            UserProtocolDialog.Builder builder = new UserProtocolDialog.Builder(this);
            builder.setPositiveButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    PreferUtil.getInstance(LoginActivity.this).putObject(PreferUtil.KEY_IS_AGREE_XIEYI, true);
                }
            });
            builder.setNegativeButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferUtil.getInstance(LoginActivity.this).putObject(PreferUtil.KEY_IS_AGREE_XIEYI, false);
                    LoginActivity.this.finish();
                }
            });
            builder.setContentClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                    intent.putExtra(WebActivity.EXTRA_KEY, new WebInfo(LoginActivity.this.getString(R.string.user_fuwu_yinsi), "http://www.mamaucan.com.cn/law"));
                    startActivity(intent);
                }
            });
            UserProtocolDialog userProtocolDialog = builder.create();
            userProtocolDialog.setCanceledOnTouchOutside(false);
            userProtocolDialog.show();
        }

    }

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @OnClick({R.id.serviceXieyi, R.id.findPwdTv, R.id.loginBtn, R.id.weixinIcon, R.id.qqIcon, R.id.registerBtn, R.id.send_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.serviceXieyi:
                Intent intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_KEY, new WebInfo(LoginActivity.this.getString(R.string.user_fuwu_yinsi), "http://www.mamaucan.com.cn/law"));
                startActivity(intent);
                break;
            case R.id.loginBtn:
                // 登录
                doLogin(phoneNumTv.getText().toString(), verfyCodeEt.getText().toString(), 0, "", "", "");
                break;
            case R.id.findPwdTv:
                //忘记密码
                startActivity(new Intent(LoginActivity.this, FindPwdActivity.class));
                break;
            case R.id.weixinIcon:
                authorization(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.qqIcon:
                authorization(SHARE_MEDIA.QQ);
                break;
            case R.id.registerBtn:
                gotoRegisterPage();
                break;
            case R.id.send_code://发送验证码
                if (StrUtil.isEmpty(phoneNumTv.getText().toString())) {
                    ToastUtil.showToast(getApplicationContext(), "请输入手机号!");
                    return;
                }
                if (!StrUtil.isMobileSimple(phoneNumTv.getText().toString())) {
                    ToastUtil.showToast(getApplicationContext(), "手机号格式错误,请重新输入!");
                    return;
                }
                if (sendCodeView.isClickable()) {
                    getSecurityCode();
                    sendCodeView.setClickable(false);
                }
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getSecurityCode() {
        final RegisterActivity.MyCountDown myCountDown = new RegisterActivity.MyCountDown(sendCodeView, 60000, 1000);
        showLoading("获取中");
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phoneNumTv.getText().toString());
        Api.getInstance().getVerifyCode(map, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                cancelLoading();
                if (isSuccess) {
                    myCountDown.start();
                    ToastUtil.showToast(LoginActivity.this, "验证码发送成功！");
                } else {
                    myCountDown.cancel();
                    myCountDown.onFinish();
                    String errMsg = result.optString("msg");
                    ToastUtil.showToast(LoginActivity.this, errMsg);
                }
                sendCodeView.setClickable(true);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                cancelLoading();
                ToastUtil.showToast(LoginActivity.this,
                        TextUtils.isEmpty(msg) ? getString(R.string.virify_code_get_fail) : msg);
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                cancelLoading();
                ToastUtil.showToast(LoginActivity.this, getString(R.string.net_error));
            }
        });
    }

    private void gotoRegisterPage() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("third_type", third_type);
        intent.putExtra("third_uid", third_uid);
        intent.putExtra("third_name", third_name);
        intent.putExtra("third_iconUrl", third_iconUrl);
        intent.putExtra("third_gender", third_gender);
        startActivity(intent);
        finish();
    }

    @Override
    public void onThirdLoginSuccess(String uid, String iconUrl, String name, String gender) {
        super.onThirdLoginSuccess(uid, iconUrl, name, gender);
        this.third_uid = uid;
        this.third_iconUrl = iconUrl;
        this.third_name = name;
        this.third_gender = gender;
        int type = 0;
        if (share_media == SHARE_MEDIA.WEIXIN) {
            type = 1;
        } else if (share_media == SHARE_MEDIA.QQ) {
            type = 2;
        } else if (share_media == SHARE_MEDIA.SINA) {
            type = 3;
        }
        this.third_type = type;
        doLogin(uid, name, type, name, iconUrl, uid);
    }

    /**
     * 登录
     *
     * @param phoneNum
     * @param pwdNum
     * @param type     0-手机密码   1-微信   2-qq
     */
    private void doLogin(String phoneNum, String pwdNum, int type, String thirdName, String thirdIcon, String thirdUniqueId) {
        if (StrUtil.isEmpty(phoneNum)) {
            ToastUtil.showToast(LoginActivity.this, R.string.input_phone_number);
            return;
        }

        if (0 == type && !StrUtil.isMobile(phoneNum)) {
            ToastUtil.showToast(LoginActivity.this, "手机号码格式不正确");
            return;
        }

        if (0 == type && StrUtil.isEmpty(pwdNum)) {
            ToastUtil.showToast(LoginActivity.this, R.string.input_pwd);
            return;
        }

        showLoading("登录中");

        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phoneNum);
        map.put("password", pwdNum);
        map.put("verification_code", pwdNum);
        map.put("type", String.valueOf(type)); //0-手机密码   1-微信   2-qq
        map.put("third_name", thirdName);
        map.put("third_icon", thirdIcon);
        map.put("third_unique_id", thirdUniqueId);
//        map.put("invitation_code",invateEditTv.getText().toString());

        Api.getInstance().doLogin(map, doLoginCallback);
    }

    //登录
    ApiCallback doLoginCallback = new ApiCallback() {
        @Override
        public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
            cancelLoading();

            int code = result.optInt("code");
            if (NetConfig.SUCCESS == code) {
                JSONObject dataJsonObj = result.optJSONObject("data");
                UserBean mUserBean = gson.fromJson(dataJsonObj.toString(), UserBean.class);
                UserUtil.saveUserInfo(mUserBean);
                LogUtil.i("用户信息 = " + mUserBean.toString());
                loginSuccess(mUserBean);
            } else if (2011 == code) {//方登陆没有绑定手机号
                gotoRegisterPage();
            } else if (2010 == code) {//手机号没有注册
                ToastUtil.showToast(LoginActivity.this, "该手机号没有注册");
            } else {
                String errMsg = result.optString("msg");
                onFail(response, errMsg);
            }
        }

        @Override
        public void onFail(Response<String> response, String msg) {
            super.onFail(response, msg);
            cancelLoading();
            ToastUtil.showToast(LoginActivity.this,
                    TextUtils.isEmpty(msg) ? getString(R.string.login_fail) : msg);
        }

        @Override
        public void onNotNetwork(String msg) {
            super.onNotNetwork(msg);
            cancelLoading();
            ToastUtil.showToast(LoginActivity.this, getString(R.string.net_error));
        }
    };

    private void loginSuccess(final UserBean mUserBean) {
        UserInfoRequest.getInstance().getUserInfo(new UserInfoRequest.OnUserInfoRequestListener() {
            @Override
            public void OnUserInfoRequestListener_result(boolean isSuccess, String msg) {
                if (isSuccess) {
                    if (1 == mUserBean.first_login) {//是否第一次登录，1首次登录，0非首次登录
                        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                        intent.putExtra(SettingsActivity.KEY_PARAMS, true);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                }

            }
        });
    }
}
