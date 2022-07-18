package cn.baby.love.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
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
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.NetConfig;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.utils.StrUtil;
import cn.baby.love.common.utils.ToastUtil;

/**
 * 找回密码页面
 */
public class FindPwdActivity extends BaseActivity {

    @BindView(R.id.phoneNumTv)
    EditText phoneNumTv;
    @BindView(R.id.passwordEdit)
    EditText passwordEditTv;
    @BindView(R.id.verfyCodeEt)
    EditText verfyCodeEt;

    @BindView(R.id.send_code)
    TextView sendCodeView;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);
        ButterKnife.bind(this);
        gson = new Gson();
    }

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @OnClick({R.id.loginBtn, R.id.leftIconLy,R.id.send_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.serviceXieyi:
                Intent intent = new Intent(FindPwdActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_KEY, new WebInfo("服务协议", "http://www.mamaucan.com.cn/law"));
                startActivity(intent);
                break;
            case R.id.loginBtn:
                // 登录
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
                finish();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getSecurityCode() {
        final MyCountDown myCountDown = new MyCountDown(sendCodeView, 60000, 1000);

        showLoading("获取中");

        HashMap<String,String> map = new HashMap<>();
        map.put("mobile",phoneNumTv.getText().toString());
        Api.getInstance().getVerifyCode(map, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                cancelLoading();
                if (isSuccess) {
                    myCountDown.start();
                    ToastUtil.showToast(FindPwdActivity.this, "验证码发送成功！");
                } else {
                    myCountDown.cancel();
                    myCountDown.onFinish();
                    String errMsg = result.optString("msg");
                    ToastUtil.showToast(FindPwdActivity.this, errMsg);
                }
                sendCodeView.setClickable(true);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                cancelLoading();
                ToastUtil.showToast(FindPwdActivity.this,
                        TextUtils.isEmpty(msg)?getString(R.string.virify_code_get_fail):msg);
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                cancelLoading();
                ToastUtil.showToast(FindPwdActivity.this, getString(R.string.net_error));
            }
        });
    }

    /**
     * 找回密码
     */
    private void doRegister() {
        String phoneNum = phoneNumTv.getText().toString();
        String pwdNum = passwordEditTv.getText().toString();
        String verifyCode = verfyCodeEt.getText().toString();
        if (StrUtil.isEmpty(phoneNum)) {
            ToastUtil.showToast(FindPwdActivity.this, R.string.input_phone_number);
            return;
        }

        if (!StrUtil.isMobile(phoneNum)) {
            ToastUtil.showToast(FindPwdActivity.this, R.string.phone_fomat_error);
            return;
        }

        if(StrUtil.isEmpty(verifyCode)){
            ToastUtil.showToast(FindPwdActivity.this, R.string.input_verifycode);
            return;
        }

        if (StrUtil.isEmpty(pwdNum)) {
            ToastUtil.showToast(FindPwdActivity.this, R.string.input_pwd);
            return;
        }

        showLoading("提交中");

        HashMap<String,String> map = new HashMap<>();
        map.put("phone",phoneNum);
        map.put("verification_code",verifyCode);
        map.put("pwd",pwdNum);

        Api.getInstance().forgotPwd(map, doLoginCallback);
    }

    ApiCallback doLoginCallback = new ApiCallback() {
        @Override
        public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
            cancelLoading();

            if (NetConfig.SUCCESS == result.optInt("code")) {
                ToastUtil.showToast(FindPwdActivity.this,"修改成功");
                finish();
            } else {
                String errMsg = result.optString("msg");
                onFail(response, errMsg);
            }
        }

        @Override
        public void onFail(Response<String> response, String msg) {
            super.onFail(response, msg);
            cancelLoading();
            ToastUtil.showToast(FindPwdActivity.this,
                    TextUtils.isEmpty(msg)?"修改失败，请稍后重试":msg);
        }

        @Override
        public void onNotNetwork(String msg) {
            super.onNotNetwork(msg);
            cancelLoading();
            ToastUtil.showToast(FindPwdActivity.this, getString(R.string.net_error));
        }
    };

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
