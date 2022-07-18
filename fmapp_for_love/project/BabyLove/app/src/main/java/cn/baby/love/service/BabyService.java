package cn.baby.love.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.baby.love.common.utils.LogUtil;
import okhttp3.Call;
import okhttp3.Response;

public class BabyService extends Service {

    private NotificationManager notificationManager;
    private Notification notification;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private final int LOOP_TIME = 10 * 1000;// 10分钟
    public BabyService() {
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        startForeground();
        super.onCreate();
    }

    private void startForeground(){
        //编译版本如果超过26就需要加上以下代码
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("CHANNEL_ID_MESSAGE", "CHANNEL_NAME_MESSAGE", NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(channel);
//            Notification notification = new Notification.Builder(getApplicationContext(), "CHANNEL_ID_MESSAGE").build();
//            startForeground(1, notification);
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timerTask != null){
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("system","baby alive");
            }
        };
        timer.schedule(timerTask, 0, LOOP_TIME);

        return super.onStartCommand(intent, flags, startId);
    }
}
