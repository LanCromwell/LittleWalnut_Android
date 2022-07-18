package cn.baby.love.common.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.baby.love.App;
import cn.baby.love.activity.LoginActivity;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.utils.PreferUtil;

/**
 * Created by wangguangbin on 2018/11/14.
 * 用户信息管理页面
 */

public class UserUtil {
    /**
     * 保存用户信息
     * @param userBean
     */
    public static void saveUserInfo(UserBean userBean){
        PreferUtil.getInstance(App.mInstance).putObject(PreferUtil.KEY_USER_BEAN,userBean);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserBean getUserInfo(){
        UserBean userBean = (UserBean) PreferUtil.getInstance(App.mInstance).getObject(PreferUtil.KEY_USER_BEAN,null);
        return userBean;
    }

    /**
     * 用户是否登录状态
     * @return
     */
    public static boolean isLogin(){
        UserBean userBean = getUserInfo();
        if(null != userBean && userBean.id > 0){
            return true;
        }
        return false;
    }

    /**
     * 获取用户id
     * @return
     */
    public static int getUserId(){
        if(UserUtil.isLogin()){
            UserBean mUserBean = getUserInfo();
            return mUserBean.id;
        }

        return -1;
    }

    public static boolean isLoginAndGoLoginActivity(Context context){
        boolean isLogin = isLogin();
        if(!isLogin){
            context.startActivity(new Intent(context,LoginActivity.class));
            return false;
        }
        return true;
    }


    //退出登录
    public static void logout(Activity activity){
        UserUtil.saveUserInfo(null);
        activity.startActivity(new Intent(activity,LoginActivity.class));
        activity.finish();
    }
}
