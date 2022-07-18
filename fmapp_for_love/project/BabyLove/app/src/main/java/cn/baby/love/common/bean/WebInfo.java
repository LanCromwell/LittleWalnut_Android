package cn.baby.love.common.bean;

import java.io.Serializable;

/**
 * @author wangxin
 * @date 2018-11-25
 * @desc web信息，用于WebActivity信息传递
 */
public class WebInfo implements Serializable {

    private String title;
    private String url;

    public WebInfo(){
    }

    public WebInfo(String title, String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
