package cn.baby.love.common.api;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.PreferUtil;

/**
 * 用户信息
 */
public class UserInfoRequest {
    private UserInfoRequest() {
    }

    public static UserInfoRequest getInstance() {
        return new UserInfoRequest();
    }

    public void getUserInfo(final OnUserInfoRequestListener mOnUserInfoRequestListener) {
        HashMap<String, String> map = new HashMap<>();
        Api.getInstance().getUserInfo(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                if (isSuccess) {
                    Gson gson = new Gson();
                    UserBean userBean = gson.fromJson(result.optJSONObject("data").toString(), UserBean.class);
                    if (null != userBean) {
                        UserUtil.saveUserInfo(userBean);
                    }

                    if (null != mOnUserInfoRequestListener) {
                        mOnUserInfoRequestListener.OnUserInfoRequestListener_result(true, "用户信息获取成功");
                    }
                } else {
                    if (null != mOnUserInfoRequestListener) {
                        mOnUserInfoRequestListener.OnUserInfoRequestListener_result(false, "用户信息获取失败");
                    }
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                if (null != mOnUserInfoRequestListener) {
                    mOnUserInfoRequestListener.OnUserInfoRequestListener_result(false, App.mInstance.getString(R.string.net_error));
                }
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                if (null != mOnUserInfoRequestListener) {
                    mOnUserInfoRequestListener.OnUserInfoRequestListener_result(false, "用户信息获取失败");
                }
            }
        });
    }

    public interface OnUserInfoRequestListener {
        void OnUserInfoRequestListener_result(boolean isSuccess, String msg);
    }

}
