package cn.baby.love.common.base;

/**
 * Created by wangguangbin on 2018/11/14.
 */

public class NetConfig {
    public static final int SUCCESS = 200;


    /**
     * 登陆
     */
    public static final String url_login = "api/v2/login";
    /**
 * 获取验证码
     */
    public static final String url_get_verify_code = "api/sms/send";
    /**
     * 注册
     */
    public static final String url_register = "api/v2/register";
    /**
     * 忘记密码
     */
    public static final String url_forgot_pwd = "api/user/forgot_pwd";
    /**
     * 三方登陆
     */
    public static final String url_third_login = "api/category/list";
    /**
     * 获取分类
     */
    public static final String url_get_classify = "api/category/list";
    /**
     * 获取角色
     */
    public static final String url_get_role = "api/role/list";
    /**
     * 获取语言
     */
    public static final String url_get_language = "api/language/list";
    /**
     * 获取音频列表
     */
    public static final String url_get_audio_list = "api/audio/list";
    /**
     * 收藏视频
     */
    public static final String url_collect = "api/audio/user_collect_audio";
    /**
     * 获取vip
     */
    public static final String url_get_vip = "api/user/receive_vip";
    /**
     * 获取用户收藏音频列表
     */
    public static final String url_get_user_collect_list = "api/audio/user_collect_audio_list";
    /**
     * 获取历史列表
     */
    public static final String url_get_history_list = "api/audio/user_listen_audio_list";
    /**
     * 提交用户反馈
     */
    public static final String url_feedback = "api/feedback/create";
    /**
     * 获取当月信息
     */
    public static final String url_get_month_info = "api/audio/reminder_today";
    /**
     * 获取用户信息
     */
    public static final String url_get_user_info = "api/user/user_info";
    /**
     * 添加试用期天数
     */
    public static final String url_get_add_days = "api/user/add_days";
    /**
     * 编辑用户信息
     */
    public static final String url_edit_user_info = "api/user/edit";
    /**
     * 搜索
     */
    public static final String url_search_info = "api/audio/search";
    /**
     * 增加播放量
     */
    public static final String url_add_play_count = "api/audio/add_play_number";
    /**
     * 分享信息获取
     */
    public static final String url_share_info = "api/poster/index";

    /**
     * 版本号
     */
    public static final String url_check_version = "api/app/info";
    /**
     * 设置位置信息
     */
    public static final String url_set_location_info = "api/user/edit_city_info";

}
