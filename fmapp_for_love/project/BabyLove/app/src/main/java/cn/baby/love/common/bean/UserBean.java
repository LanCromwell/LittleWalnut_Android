package cn.baby.love.common.bean;

import java.io.Serializable;

/**
 * @author wangxin
 * @date 2018-11-7
 * @desc 用户信息
 */
public class UserBean implements Serializable {


    public int id;
    public String phone;
    public String avatar;
    public int role_id;
    public int language_id;
    public int add_time;
    public String role_name;
    public String language_name;
    public String token;
    //是否第一次登录，1首次登录，0非首次登录
    public int first_login;
    public String register_date;//已经学习28天
    public String birthday_date;//宝宝已经4岁7月7天
    public long child_birthday; //1560349408
    //是否领取了Vip，0未领取，1领取
    public int is_receive_vip;
    //渠道名称
    public String channel_name;
    //渠道号
    public String channel_code;

    //邀请信息
    public InvateInfo invite_info;
    //是否设置位置信息0未设置，1设置
    public int is_set_location;

    //有赞
    public YouzanInfo youzan_info;

    //是否免费,等于1  就是  一直免费使用app
    public int is_free;

    //剩余天数
    public int remainder_days;
    //分享获取的天数
    public int user_share_get_day;
}
