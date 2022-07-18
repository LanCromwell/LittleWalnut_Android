package cn.baby.love.common.api;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

import cn.baby.love.common.base.BaseApi;
import cn.baby.love.common.base.NetConfig;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.StrUtil;

/**
 * @author wangxin
 * @date 2018-11-7
 * @desc API请求入口
 */
public class Api extends BaseApi {

    private static Api api = new Api();

    private Api(){
    }

    public static Api getInstance(){
        return api;
    }

    public void test1(Context context, HashMap<String, String> params, ApiCallback callback){
        this.call(context,"http://httpbin.org:80/delay/3" , params, callback);
    }

    public void test2(HashMap<String, String> params, ApiCallback callback){
        this.call("http://httpbin.org:80/delay/6" , params, callback);
    }

    public void doLogout(HashMap<String, String> params, ApiCallback callback){
        this.call("/user/logout", params, callback);
    }

    /**
     * 登录
     * @param callback
     */
    public void doLogin(HashMap<String,String> map, ApiCallback callback){
        this.call(NetConfig.url_login, map, callback);
    }

    /**
     * 获取验证码
     * @param callback
     */
    public void getVerifyCode(HashMap<String,String> map, ApiCallback callback){
        this.call(NetConfig.url_get_verify_code, map, callback);
    }

    /**
     * 注册
     * @param callback
     */
    public void doRegister(HashMap<String,String> map, ApiCallback callback){
        this.call(NetConfig.url_register, map, callback);
    }
    /**
     * 忘记密码
     * @param callback
     */
    public void forgotPwd(HashMap<String,String> map, ApiCallback callback){
        this.call(NetConfig.url_forgot_pwd, map, callback);
    }

    /**
     * 收藏
     */
    public void collect(HashMap<String, String> map, ApiCallback callback){
        this.call(NetConfig.url_collect, map, callback);
    }
    /**
     * 领取vip
     */
    public void getVip(HashMap<String, String> map, ApiCallback callback){
        this.call(NetConfig.url_get_vip, map, callback);
    }

    /**
     * 获取我的收藏
     * @param page
     * @param callback
     */
    public void getCollectList(int page,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        map.put("page",String.valueOf(page));
        map.put("page_size","10");
        this.call(NetConfig.url_get_user_collect_list, map, callback);
    }

    /**
     * 获取历史列表
     * @param page
     * @param callback
     */
    public void getHistoryList(int page,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        map.put("page",String.valueOf(page));
        map.put("page_size","10");
        this.call(NetConfig.url_get_history_list, map, callback);
    }

    /**
     * 意见反馈
     */
    public void feedback(String content,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        map.put("feedback_content",content);
        this.call(NetConfig.url_feedback, map, callback);
    }

    /**
     * 获取角色信息
     * @param callback
     */
    public void getRoleInfos(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_role, map, callback);
    }
    /**
     * 获取用户信息
     * @param callback
     */
    public void getUserInfo(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_user_info, map, callback);
    }
    /**
     * 获取用户信息
     * @param callback
     */
    public void addDays(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_add_days, map, callback);
    }

    /**
     * 获取语言信息
     * @param callback
     */
    public void getLanguageInfos(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_language, map, callback);
    }

    /**
     * 获取分类信息
     * @param callback
     */
    public void getCategoryList(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_classify, map, callback);
    }

    /**
     * 获取首页日历信息
     * @param callback
     */
    public void getTodayInfo(ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        this.call(NetConfig.url_get_month_info, map, callback);
    }
    /**
     * 编辑用户信息
     * @param callback
     */
    public void updateUserInfo(int language_id,int role_id,long child_birthday,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        map.put("language_id",String.valueOf(language_id));
        map.put("role_id",String.valueOf(role_id));
        map.put("child_birthday",String.valueOf(child_birthday));
        this.call(NetConfig.url_edit_user_info, map, callback);
    }
    /**
     * 搜索
     * @param callback
     */
    public void search(int page,String searchContent,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        if(UserUtil.isLogin()){
            UserBean userBean = UserUtil.getUserInfo();
            map.put("language_id",String.valueOf(userBean.language_id));
            map.put("role_id",String.valueOf(userBean.role_id));
        }
        map.put("page",String.valueOf(page));
        map.put("page_size","10");
        map.put("search_content",searchContent);
        this.call(NetConfig.url_search_info, map, callback);
    }

    /**
     * 获取音频列表
     * @param callback
     */
    public void getExportInfoList(int page,int category_id,int pid,ApiCallback callback){
        HashMap<String,String> map = new HashMap<>();
        if(UserUtil.isLogin()){
            UserBean userBean = UserUtil.getUserInfo();
            map.put("language_id",String.valueOf(userBean.language_id));
            map.put("role_id",String.valueOf(userBean.role_id));
            map.put("child_birthday",String.valueOf(userBean.birthday_date)); //需要传入一个long值
        }
        map.put("page",String.valueOf(page));
        map.put("page_size","10");
        map.put("type","1");
        map.put("type",String.valueOf(category_id));
        map.put("pid",String.valueOf(pid));
        this.call(NetConfig.url_get_audio_list, map, callback);
    }

    /**
     * 播放量接口
     */
    public void addPlayCount(int audio_id){
        HashMap<String,String> map = new HashMap<>();
        map.put("audio_id",String.valueOf(audio_id));
        this.call(NetConfig.url_add_play_count, map, null);
    }

    /**
     * 分享信息获取
     */
    public void getShareInfo(ApiCallback callback){
        this.call(NetConfig.url_share_info, null, callback);
    }

    /**
     * 设置位置信息
     */
    public void setLocation(HashMap<String,String> map,ApiCallback callback){
        this.call(NetConfig.url_set_location_info, map, callback);
    }

}
