package cn.baby.love.common.api;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.logging.Logger;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.PreferUtil;

/**
 * 位置信息设置接口
 */
public class LocationSetRequest {
    private LocationSetRequest() {
    }

    public static LocationSetRequest getInstance() {
        return new LocationSetRequest();
    }

    public void setLocationInfo(String province, String city,
                                String county, String detailed_location) {
        HashMap<String, String> map = new HashMap<>();
        map.put("province",province);
        map.put("city",city);
        map.put("county",county);
        map.put("detailed_location",detailed_location);
        LogUtil.i("位置信息 = "+map);
        Api.getInstance().setLocation(map,new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                LogUtil.i("位置设置成功"+result);
                if(isSuccess){
                    UserBean userBean = UserUtil.getUserInfo();
                    userBean.is_set_location = 1;
                    UserUtil.saveUserInfo(userBean);
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                LogUtil.i("位置设置失败"+msg);
            }
        });
    }
}
