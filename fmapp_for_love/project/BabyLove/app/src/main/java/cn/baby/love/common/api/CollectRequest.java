package cn.baby.love.common.api;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.base.NetConfig;
import cn.baby.love.common.manager.UserUtil;

/**
 * 收藏请求
 */
public class CollectRequest {
    private CollectRequest() {
    }

    public static CollectRequest getInstance() {
        return new CollectRequest();
    }

    public void collect(final boolean isCollect, int audio_id, final OnCollectRequestListener mOnCollectRequestListener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("audio_id", String.valueOf(audio_id));
        Api.getInstance().collect(map, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                if (null != mOnCollectRequestListener) {
                    if (isSuccess) {
                        mOnCollectRequestListener.onCollectRequestListener_result(true, isCollect ? "收藏已移除" : "已收藏");
                    } else {
                        mOnCollectRequestListener.onCollectRequestListener_result(false, isCollect ? "取消失败" : "收藏失败");
                    }
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                if (null != mOnCollectRequestListener) {
                    mOnCollectRequestListener.onCollectRequestListener_result(false, App.mInstance.getString(R.string.net_error));
                }
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                if (null != mOnCollectRequestListener) {
                    mOnCollectRequestListener.onCollectRequestListener_result(false, isCollect ? "取消失败" : "收藏失败");
                }
            }
        });
    }

    public interface OnCollectRequestListener {
        void onCollectRequestListener_result(boolean isSuccess, String msg);
    }

}
