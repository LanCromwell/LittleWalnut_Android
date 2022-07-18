package cn.baby.love.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.dialog.CustomDialog;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.leftIcon)
    ImageView leftIcon;
    @BindView(R.id.centerTv)
    TextView centerTv;
    @BindView(R.id.rightTv1)
    TextView rightTv1;
    @BindView(R.id.rightTv2)
    TextView rightTv2;
    @BindView(R.id.inputEd)
    EditText inputEd;
    @BindView(R.id.Iconerweima)
    ImageView Iconerweima;
    @BindView(R.id.weixinTv)
    TextView weixinTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        initTitle("意见反馈");
        leftIcon.setImageResource(R.drawable.icon_back_black);
        centerTv.setTextColor(getResources().getColor(R.color.color_31));

    }

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @OnClick(R.id.commitBtn)
    public void onViewClicked() {
        String contentStr = inputEd.getText().toString();
        if (TextUtils.isEmpty(contentStr)) {
            ToastUtil.showToast(this, "请输入您的意见");
            return;
        }
        showLoading();
        Api.getInstance().feedback(contentStr, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess,JSONObject result) {
                cancelLoading();
                showDialog();
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                cancelLoading();
                ToastUtil.showToast(FeedBackActivity.this, R.string.net_error);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                cancelLoading();
                ToastUtil.showToast(FeedBackActivity.this, getString(R.string.commit_fail));
            }
        });
    }

    private void showDialog(){
        CustomDialog.confirmOneBtn(this, getString(R.string.your_feedback_success), getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
    }

}
