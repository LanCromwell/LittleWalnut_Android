package cn.baby.love.common.base;

import java.io.File;

/**
 * @author wangxin
 * @date 2018-11-8
 * @desc 全局配置文件
 */
public class BaseConfig {

    /**
     * 是否为调试环境
     */
    public static final boolean isDebug = false;

    /**
     * App跟目录
     */
    public final static String ROOT_DIR = File.separator + "baby" + File.separator;

    /**
     * 刷新首页数据
     */
    public final static String ACTION_REFRESH_TODAY_INFO = "action_refresh_today_info";

    /**
     * 通知其他播放器暂停播放
     */
    public final static String ACTION_NOTIFY_OTHER_PLAYER_PAUSE = "action_notify_other_player_pause";
    /**
     * 分享成功
     */
    public final static String ACTION_NOTIFY_SHARE_SUCCESS = "action_notify_share_success";

    //有赞clientId
    public final static String YOU_ZAN_CLIENT_ID = "03e3b11bd9a1ea2b97";
    //有赞商铺首页地址
    public final static String YOU_ZAN_SHOP_URL = "http://shop44839580.youzan.com/v2/showcase/homepage?alias=NXuhsqn2VB";

}
