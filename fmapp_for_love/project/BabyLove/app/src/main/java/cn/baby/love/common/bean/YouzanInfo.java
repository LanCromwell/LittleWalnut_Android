package cn.baby.love.common.bean;

import java.io.Serializable;

/**
 * 有赞信息
 */
public class YouzanInfo implements Serializable {
    public String access_token;
    public String cookie_key;
    public String cookie_value;
    public String shop_href;//店铺地址
    public String customer_service_href;//客服地址

    @Override
    public String toString() {
        return "YouzanInfo{" +
                "access_token='" + access_token + '\'' +
                ", cookie_key='" + cookie_key + '\'' +
                ", cookie_value='" + cookie_value + '\'' +
                ", shop_href='" + shop_href + '\'' +
                '}';
    }
}
