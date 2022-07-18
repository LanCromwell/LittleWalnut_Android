package cn.baby.love.common.api;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.bean.LanguageInfo;
import cn.baby.love.common.bean.RoleInfo;
import cn.baby.love.common.utils.PreferUtil;

/**
 * 角色和语言信息请求
 */
public class RoleLanguageRequest {

    private final Gson gson;

    private RoleLanguageRequest() {
        gson = new Gson();
    }

    public static RoleLanguageRequest getInstance() {
        return new RoleLanguageRequest();
    }

    /**
     * 获取角色信息
     * @param mOnCollectRequestListener
     */
    public void getRoleInfos(final OnCollectRequestListener mOnCollectRequestListener) {
        Api.getInstance().getRoleInfos(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {

                if (isSuccess) {
                    RoleInfo roleInfos = gson.fromJson(result.toString(), RoleInfo.class);
                    if (null != roleInfos && null != roleInfos.data && roleInfos.data.size() > 0){
                        PreferUtil.getInstance(App.mInstance).putObject(PreferUtil.KEY_ROLE_INFOS, roleInfos.data);
                        if (null != mOnCollectRequestListener) {
                            mOnCollectRequestListener.onCollectRequestListener_result(true, "获取成功");
                        }
                    }
                } else {
                    if (null != mOnCollectRequestListener) {
                        mOnCollectRequestListener.onCollectRequestListener_result(false, "获取失败");
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
                    mOnCollectRequestListener.onCollectRequestListener_result(false, "获取失败");
                }
            }
        });
    }

    /**
     * 获取语言信息
     * @param mOnCollectRequestListener
     */
    public void getLaugage(final OnCollectRequestListener mOnCollectRequestListener) {
        Api.getInstance().getLanguageInfos(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {

                if (isSuccess) {
                    LanguageInfo lanInfos = gson.fromJson(result.toString(), LanguageInfo.class);
                    if (null != lanInfos && null != lanInfos.data && lanInfos.data.size() > 0){
                        PreferUtil.getInstance(App.mInstance).putObject(PreferUtil.KEY_LANGUAGE_INFOS, lanInfos.data);
                        if (null != mOnCollectRequestListener) {
                            mOnCollectRequestListener.onCollectRequestListener_result(true, "获取成功");
                        }
                    }
                } else {
                    if (null != mOnCollectRequestListener) {
                        mOnCollectRequestListener.onCollectRequestListener_result(false, "获取失败");
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
                    mOnCollectRequestListener.onCollectRequestListener_result(false, "获取失败");
                }
            }
        });
    }

    public interface OnCollectRequestListener {
        void onCollectRequestListener_result(boolean isSuccess, String msg);
    }

}
