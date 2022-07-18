package cn.baby.love.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.text.NumberFormat;

import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.NetworkUtil;

/**
 * @author wangxin
 * @date 2018-11-7
 * @desc 下载服务(后台运行)
 */
public class DownloadService extends Service {

    private final String TAG = "DownloadService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(NetworkUtil.isAvailable(this)){
            return Service.START_STICKY;
        }
        if(intent == null){
            return Service.START_STICKY;
        }
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        startDownload(url, title);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 启动一个下载任务
     * @param url
     * @param title
     */
    private void startDownload(String url, String title){
        final NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        OkGo.<File>get(url)
                .tag(this)
                .headers("","")
                .params("", "")
                .execute(new FileCallback(BaseConfig.ROOT_DIR+"/download/", title) {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        LogUtil.d(TAG, "正在下载中");
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        LogUtil.d(TAG, "下载完成");
                    }

                    @Override
                    public void onError(Response<File> response) {
                        LogUtil.d(TAG, "下载出错");
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        //已下载/总容量
                        LogUtil.d(TAG, String.format("%s/%s",downloadLength, totalLength));

                        //下载速度
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        LogUtil.d(TAG, String.format("%s/s",speed));

                        LogUtil.d(TAG, numberFormat.format(progress.fraction*100));

                        //Intent intent = new Intent();
                        //intent.putExtra("progress", (progress.fraction*100));
                        //sendBroadcast(intent);
                    }
                });
    }

}
