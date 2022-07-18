package cn.baby.love.common.api;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import cn.baby.love.App;
import cn.baby.love.R;

/**
 * 领取了Vip操作
 */
public class GetVipRequest {
    private GetVipRequest() {
    }

    public static GetVipRequest getInstance() {
        return new GetVipRequest();
    }

    public void getVip(final OnVipGetRequestListener mOnVipGetRequestListener) {
        HashMap<String, String> map = new HashMap<>();
        Api.getInstance().getVip(map, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                if (null != mOnVipGetRequestListener) {
                    mOnVipGetRequestListener.onOnVipGetRequestListenerr_result(true, "VIP领取成功");
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                if (null != mOnVipGetRequestListener) {
                    mOnVipGetRequestListener.onOnVipGetRequestListenerr_result(false, App.mInstance.getString(R.string.net_error));
                }
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                if (null != mOnVipGetRequestListener) {
                    mOnVipGetRequestListener.onOnVipGetRequestListenerr_result(false, "VIP领取失败，请稍后重试！");
                }
            }
        });
    }

    public interface OnVipGetRequestListener {
        void onOnVipGetRequestListenerr_result(boolean isSuccess, String msg);
    }

}
