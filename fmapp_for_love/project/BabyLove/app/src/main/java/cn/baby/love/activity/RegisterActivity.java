package cn.baby.love.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

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
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.NetConfig;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.StrUtil;
import cn.baby.love.common.utils.ToastUtil;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.phoneNumTv)
    EditText phoneNumTv;
    @BindView(R.id.verfyCodeEt)
    EditText verfyCodeEt;
    @BindView(R.id.invateEditTv)
    EditText invateEditTv;

    @BindView(R.id.send_code)
    TextView sendCodeView;
    @BindView(R.id.stitle)
    TextView pageTitle;
    @BindView(R.id.loginBtn)
    Button registBtn;

    private Gson gson;
    private String third_name;
    private String third_iconUrl;
    private String third_gender;
    private int third_type = 0;
    private String third_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        gson = new Gson();
        Intent intent = getIntent();
        if (null != intent) {
            third_type = intent.getIntExtra("third_type", -1);
            third_uid = intent.getStringExtra("third_uid");
            third_name = intent.getStringExtra("third_name");
            third_iconUrl = intent.getStringExtra("third_iconUrl");
            third_gender = intent.getStringExtra("third_gender");
        }
        if (TextUtils.isEmpty(third_uid)) {
            pageTitle.setText("用户注册");
            registBtn.setText("注册");
        } else {
            pageTitle.setText("绑定账号");
            registBtn.setText("绑定");
        }
    }

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @OnClick({R.id.loginBtn, R.id.leftIconLy, R.id.send_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                // 注册
                doRegister();
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
            case R.id.leftIconLy:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        super.onBackPressed();
    }

    /**
     * 获取验证码
     */
    private void getSecurityCode() {
        final MyCountDown myCountDown = new MyCountDown(sendCodeView, 60000, 1000);

        showLoading("获取中");

        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phoneNumTv.getText().toString());
        Api.getInstance().getVerifyCode(map, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                cancelLoading();
                if (isSuccess) {
                    myCountDown.start();
                    ToastUtil.showToast(RegisterActivity.this, "验证码发送成功！");
                } else {
                    myCountDown.cancel();
                    myCountDown.onFinish();
                    String errMsg = result.optString("msg");
                    ToastUtil.showToast(RegisterActivity.this, errMsg);
                }
                sendCodeView.setClickable(true);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                cancelLoading();
                ToastUtil.showToast(RegisterActivity.this,
                        TextUtils.isEmpty(msg) ? getString(R.string.virify_code_get_fail) : msg);
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                cancelLoading();
                ToastUtil.showToast(RegisterActivity.this, getString(R.string.net_error));
            }
        });
    }


//    @Override
//    public void onThirdLoginSuccess(String uid, String iconUrl, String name, String gender) {
//        super.onThirdLoginSuccess(uid, iconUrl, name, gender);
//        int type = 0;
//        if (share_media == SHARE_MEDIA.WEIXIN){
//            type = 1;
//        }else if(share_media == SHARE_MEDIA.QQ){
//            type = 2;
//            }else if(share_media == SHARE_MEDIA.SINA){
//            type = 3;
//        }
//        doLogin(uid, name,type,name,iconUrl,uid);
//    }

    /**
     * 注册
     */
    private void doRegister() {
        String phoneNum = phoneNumTv.getText().toString();
        String verifyCode = verfyCodeEt.getText().toString();
        if (StrUtil.isEmpty(phoneNum)) {
            ToastUtil.showToast(RegisterActivity.this, R.string.input_phone_number);
            return;
        }

        if (!StrUtil.isMobile(phoneNum)) {
            ToastUtil.showToast(RegisterActivity.this, R.string.phone_fomat_error);
            return;
        }

        if (StrUtil.isEmpty(verifyCode)) {
            ToastUtil.showToast(RegisterActivity.this, R.string.input_verifycode);
            return;
        }
        showLoading("绑定中");

        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phoneNum);
        map.put("password", "");
        map.put("verification_code", verifyCode);
        map.put("type", String.valueOf(third_type)); //0-手机密码   1-微信   2-qq
        map.put("third_name", third_name);
        map.put("third_icon", third_iconUrl);
        map.put("third_unique_id", third_uid);
        map.put("invitation_code", invateEditTv.getText().toString());

        Api.getInstance().doRegister(map, doLoginCallback);
    }

    //注册
    ApiCallback doLoginCallback = new ApiCallback() {
        @Override
        public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
            cancelLoading();

            if (NetConfig.SUCCESS == result.optInt("code")) {
                if (TextUtils.isEmpty(third_uid)) {
                    ToastUtil.showToast(RegisterActivity.this, "注册成功");
                } else {
                    ToastUtil.showToast(RegisterActivity.this, "绑定成功");
                }

                JSONObject dataJsonObj = result.optJSONObject("data");
                UserBean mUserBean = gson.fromJson(dataJsonObj.toString(), UserBean.class);
                UserUtil.saveUserInfo(mUserBean);
                LogUtil.i("用户信息 = " + mUserBean.toString());

                registerSuccess();
            } else {
                String errMsg = result.optString("msg");
                onFail(response, errMsg);
            }
        }

        @Override
        public void onFail(Response<String> response, String msg) {
            super.onFail(response, msg);
            cancelLoading();
            if (TextUtils.isEmpty(third_uid)) {
                ToastUtil.showToast(RegisterActivity.this,
                        TextUtils.isEmpty(msg) ? "注册失败，请稍后重试" : msg);
            } else {
                ToastUtil.showToast(RegisterActivity.this,
                        TextUtils.isEmpty(msg) ? "绑定失败，请稍后重试" : msg);
            }

        }

        @Override
        public void onNotNetwork(String msg) {
            super.onNotNetwork(msg);
            cancelLoading();
            ToastUtil.showToast(RegisterActivity.this, getString(R.string.net_error));
        }
    };

    private void registerSuccess() {
//        UserInfoRequest.getInstance().getUserInfo(new UserInfoRequest.OnUserInfoRequestListener() {
//            @Override
//            public void OnUserInfoRequestListener_result(boolean isSuccess, String msg) {
//                //注册成功，直接进入设置页码
//                if(isSuccess){
        Intent intent = new Intent(RegisterActivity.this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.KEY_PARAMS, true);
        startActivity(intent);
        finish();
//                }
//            }
//        });
    }

    /**
     * 验证码倒计时工具类
     */
    static class MyCountDown extends CountDownTimer {
        TextView textView;

        public MyCountDown(TextView textView, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.textView = textView;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textView.setClickable(false); //设置不可点击
            textView.setText(millisUntilFinished / 1000 + " s"); //设置倒计时时间  
            //SpannableString spannableString = new SpannableString(textView.getText().toString()); //获取按钮上的文字  
            //ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            //spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色  
            //textView.setText(spannableString);
        }

        @Override
        public void onFinish() {
            textView.setText("重新获取");
            textView.setClickable(true);//重新获得点击
        }
    }
}
