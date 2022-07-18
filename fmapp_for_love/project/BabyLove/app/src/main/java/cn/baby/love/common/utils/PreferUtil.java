package cn.baby.love.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author wangguangbin
 * @date 2018-11-6
 * @desc sharePreference 数据缓存xml
 */
public class PreferUtil {
    private static final String PREFERENCE_NAME = "jstyle_yun";
    private static PreferUtil preferenceUtil;
    private SharedPreferences sp;
    private Editor ed;

    /**
     * 用户信息
     */
    public static final String KEY_USER_BEAN = "key_user_bean";

    /**
     * 是否开启提醒
     */
    public static final String KEY_IS_OPEN_REMIND = "key_is_open_remind";
    /**
     * 是否显示引导页
     */
    public static final String KEY_IS_OPEN_GUIDE_PAGE = "key_is_open_guide_page1";

    /**
     * 是否显示主页面的引导页
     */
    public static final String KEY_IS_OPEN_GUIDE_PAGE_FOR_MAIN = "key_is_open_guide_page_for_main";

    /**
     * 角色信息
     */
    public static final String  KEY_ROLE_INFOS = "key_role_infos";
    /**
     * 语言信息
     */
    public static final String  KEY_LANGUAGE_INFOS = "key_language_infos";

    /**
     * 当前角色信息
     */
    public static final String  KEY_CUR_ROLE_INFOS = "key_cur_role_infos";
    /**
     * 当前语言信息
     */
    public static final String  KEY_CUR_LANGUAGE_INFOS = "key_cur_language_infos";
    /**
     * 当天是否第一次播放
     */
    public static final String  KEY_IS_FIRST_PLAY_TODY = "key_today_is_first_play";
    /**
     * 是否同意了隐私协议
     */
    public static final String KEY_IS_AGREE_XIEYI = "key_is_agree_xie_yi";

    private PreferUtil(Context context) {
        init(context);
    }

    public void init(Context context) {
        if (sp == null || ed == null) {
            try {
                sp = context.getSharedPreferences(PREFERENCE_NAME, 0);
                ed = sp.edit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInit(){
        if(sp == null || ed == null){
            return true;
        }
        return false;
    }

    public static PreferUtil getInstance(Context context) {
        if (preferenceUtil == null) {
            preferenceUtil = new PreferUtil(context);
        }
        if(preferenceUtil.isInit()){
            preferenceUtil.init(context);
        }
        return preferenceUtil;
    }

    /**
     * 可以存放一切的内容
     * @param key
     * @param obj
     * @return
     */
    public boolean putObject(String key, Object obj) {
        try {
            if(sp == null){
                return false;
            }
            if(StrUtil.isEmpty(key)){
                return false;
            }
            if (obj == null) {
                Editor e = sp.edit();
                e.putString(key, "");
                return e.commit();
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(obj);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
                String objectBase64 = new String(Base64.encode(baos.toByteArray(),
                        Base64.DEFAULT));
                Editor e = sp.edit();
                e.putString(key, objectBase64);
                return e.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Object getObject(String key, Object defaultValue) {
        try {
            if(sp == null){
                return defaultValue;
            }
            if(StrUtil.isEmpty(key)){
                return false;
            }
            String objectBase64 = sp.getString(key, "");
            byte[] base64Bytes = Base64.decode(objectBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois;
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
