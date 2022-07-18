package cn.baby.love.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.utils.UmentUtils;

/**
 * 分享测试类
 */
public class ShareTestActivity extends BaseActivity {

    @BindView(R.id.shareBtn)
    Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_test);
        ButterKnife.bind(this);

        Api.getInstance().test1(this, null, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess,JSONObject result) {
                ToastUtil.showToast(ShareTestActivity.this, "延迟了3秒");
            }
        });

        Api.getInstance().test2(null, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                ToastUtil.showToast(ShareTestActivity.this, "延迟了6秒,未中断请求。。。");
            }
        });
    }

    @OnClick(R.id.shareBtn)
    public void doShare(){
        UmentUtils.openSharePanel(this);
    }

    //QQ与新浪不需要添加Activity，但需要在使用QQ分享或者授权的Activity中，添加：
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
