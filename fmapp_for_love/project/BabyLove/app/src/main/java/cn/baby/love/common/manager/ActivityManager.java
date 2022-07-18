package cn.baby.love.common.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 王鑫 on 2017-6-20.
 */

public class ActivityManager {

    private static final List<Activity> activityList = new ArrayList<>();

    public static void add(Activity activity) {
        if (activity != null)
            activityList.add(activity);
    }

    public static boolean is(Activity activity) {
        if (activity == null)
            return false;
        return activityList.contains(activity);
    }


    public static boolean is(Class<?> className){
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            if(activity.getClass() == className){
                return true;
            }
        }
        return false;
    }
    public static void remove(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity.finish();
        }
    }

    public static void remove(Class<?> className){
        Iterator<Activity> it = activityList.iterator();
        while(it.hasNext()){
            Activity activity = it.next();
            if(activity.getClass() == className){
                activity.finish();
                it.remove();
            }
        }
    }

    public static void clear() {
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            activity.finish();
        }
        activityList.clear();
    }

    /**
     * 清除其他页面
     */
    public static void clearOthers(Activity curAct) {
        Iterator<Activity> it = activityList.iterator();
        while(it.hasNext()){
            Activity activity = it.next();
            if (curAct != activity){
                activity.finish();
                it.remove();
            }
        }
    }

}
