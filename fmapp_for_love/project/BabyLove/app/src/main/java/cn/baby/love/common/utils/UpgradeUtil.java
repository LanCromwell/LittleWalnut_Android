package cn.baby.love.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.allenliu.versionchecklib.core.http.HttpHeaders;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;

import org.json.JSONException;
import org.json.JSONObject;

import cn.baby.love.common.base.BaseApi;
import cn.baby.love.common.base.NetConfig;

/**
 * Created by wangguangbin on 2018/11/7.
 * https://github.com/alanforgithub/CheckVersionLib
 * 版本管理
 */
public class UpgradeUtil {
    public static void checkVersion(final Context context) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("bxg-os", "Android");
        headers.put("bxg-version", "3.8.0");
        headers.put("bxg-platform", "AndroidMobile");
        headers.put("bxg-imei", AppUtil.getIMEI(context));

        HttpParams params = new HttpParams();
        params.put("bxg-os", "Android");
        params.put("bxg-version", "3.8.0");
        params.put("bxg-platform", "AndroidMobile");
        params.put("bxg-imei", AppUtil.getIMEI(context));
        AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(BaseApi.getApi(NetConfig.url_check_version))
                .setRequestParams(params)
                .setHttpHeaders(headers)
                .setRequestMethod(HttpRequestMethod.POST)
                .request(new RequestVersionListener() {
                    @Override
                    public UIData onRequestVersionSuccess(String result) {Log.i("version", result);
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        //如果是最新版本直接return null
                        String url = null;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (NetConfig.SUCCESS == jsonObject.optInt("code")) {
                                JSONObject dataJson = jsonObject.optJSONObject("data");
                                int code = dataJson.optInt("app_version_code");
                                if (code > AppUtil.getAppVersionCode(context)) {
                                    url = dataJson.optString("app_download_LINK");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (TextUtils.isEmpty(url)) {
                            return null;
                        } else {
                            UIData mUIData = UIData.create();
                            mUIData.setDownloadUrl(url);
                            mUIData.setTitle("版本更新");
                            mUIData.setContent("有新版本啦，快来更新");
                            return mUIData;
                        }
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                }).executeMission(context);
    }
}
