package cn.baby.love.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by wangguangbin on 2019/3/25.
 */
public class ServiceUtils {
    /**
     * 启动服务
     */
    public static void startService(Context context,Class serviceClass) {
        Intent service = new Intent(context, serviceClass);
        context.startService(service);
        //编译版本如果超过26就要使用以下代码
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(service);
//        }else{
//            context.startService(service);
//        }
    }
}
