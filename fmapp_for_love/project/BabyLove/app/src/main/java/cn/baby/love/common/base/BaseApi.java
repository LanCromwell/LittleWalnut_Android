package cn.baby.love.common.base;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

import cn.baby.love.App;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.AppUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.NetworkUtil;

/**
 * @author wangxin
 * @date 2018-11-7
 * @desc Api基类, 统一管理请求
 */
public abstract class BaseApi {

    private final String TAG = "BaseApi";

    private final int STATUS_SUCCESS = NetConfig.SUCCESS;

    private final static HashMap<String, HashSet<String>> callTagMap = new HashMap<>();

    /**
     * 记录OkGo Request.tag名称，用于取消请求
     *
     * @param context
     * @param tagName
     */
    private void addCallTag(Context context, String tagName) {
        if (context == null)
            return;
        HashSet<String> tags = callTagMap.get(context.getClass().getName());
        if (tags == null) {
            tags = new HashSet<>();
            callTagMap.put(context.getClass().getName(), tags);
        }
        tags.add(tagName);
    }

    /**
     * 取消请求
     *
     * @param context
     */
    protected void cancelCall(Context context) {
        if (context == null)
            return;
        HashSet<String> tags = callTagMap.get(context.getClass().getName());
        if (tags != null) {
            for (String tag : tags) {
                OkGo.getInstance().cancelTag(tag);
            }
            callTagMap.remove(context.getClass().getName());
        }
    }

    /**
     * 获取格式化的请求地址 domain+uri
     *
     * @param uri
     * @return
     */
    public static String getApi(String uri) {
        if (uri.startsWith("http://") || uri.startsWith("https://")) {
            return uri;
        }
        if (BaseConfig.isDebug) {
            return String.format("%s%s", "http://test.api.mamaucan.cn/", uri);
        } else {
            return String.format("%s%s", "http://api.mamaucan.cn/", uri);
        }
    }

    /**
     * Http请求 页面关闭后（onDestory）会中断http请求，再次必须传入context用来标记请求tag
     *
     * @param context
     * @param uri
     * @param params
     * @param cb
     */
    protected void call(Context context, final String uri, HashMap<String, String> params, final ApiCallback cb) {
        addCallTag(context, uri);
        this.call(uri, params, cb);
    }

    /**
     * Http请求
     *
     * @param uri
     * @param params
     * @param cb
     */
    protected void call(final String uri, HashMap<String, String> params, final ApiCallback cb) {
        if (!NetworkUtil.isAvailable(App.mInstance) && null != cb) {
            cb.onNotNetwork("无网络!");
            return;
        }
        if (params == null) {
            params = new HashMap();
        }
        //公共参数
        if (UserUtil.isLogin()) {
            params.put("user_id",String.valueOf(UserUtil.getUserInfo().id));
            params.put("token", UserUtil.getUserInfo().token);
            params.put("channel_name",UserUtil.getUserInfo().channel_name);
            params.put("channel_code",UserUtil.getUserInfo().channel_code);
        }

        params.put("phone_model", AppUtil.getSystemModel());//手机型号
        params.put("phone_brand", AppUtil.getDeviceBrand());//手机厂商
        params.put("operator", AppUtil.getOperatorName(App.mInstance));//运营商信息

        HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
        headers.put("source_type", "android");
        headers.put("baby-version", AppUtil.getAppVersionName(App.mInstance));
        headers.put("baby-platform", "AndroidMobile");
        headers.put("baby-imei", AppUtil.getIMEI(App.mInstance));
        headers.put("phone_model", AppUtil.getSystemModel());//手机型号
        headers.put("phone_brand", AppUtil.getDeviceBrand());//手机厂商
        headers.put("operator", AppUtil.getOperatorName(App.mInstance));//手机
        headers.put("Accept", "application/vnd.myapp.v2+json");//接口版本


        OkGo.<String>post(getApi(uri))
                .tag(uri)
                .headers(headers)
                .params(params)
                .execute(new StringCallback() {

                    /**注意*这里已经是在主线程(可以在回调方法里直接操作UI)了**/

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (cb == null) return;
                        String result = response.body();
                        try {
                            JSONObject json = new JSONObject(result);
                            cb.onSuccess(response, (NetConfig.SUCCESS == json.optInt("code")),json);
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                            cb.onFail(response, "数据请求失败");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        if (cb == null) return;
                        LogUtil.e(TAG, response.getException().getMessage());
                        cb.onFail(response, "数据请求失败");
                    }

                    @Override
                    public void onFinish() {
                        if (cb == null) return;
                        cb.onFinish();
                    }
                });
    }
}
