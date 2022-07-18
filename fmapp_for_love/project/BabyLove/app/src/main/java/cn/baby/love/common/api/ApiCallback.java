package cn.baby.love.common.api;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

/**
 * @author wangxin
 * @date 2018-11-9
 * @desc 接口回调处理类
 */
public abstract class ApiCallback {
    public void onNotNetwork(String msg){
    }
    public void onFinish(){
    }
    public void onFail(Response<String> response, String msg){
    }
    public abstract void onSuccess(Response<String> response,boolean isSuccess, JSONObject result);
}
