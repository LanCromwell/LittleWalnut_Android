package cn.baby.love.common.utils;

import android.os.Environment;
import android.text.format.Formatter;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.text.NumberFormat;

import cn.baby.love.App;
import cn.baby.love.common.base.BaseConfig;

/**
 * @author wangxin
 * @date 2018-11-8
 * @desc 异步下载
 */
public class DownloadUtil {

    private final String TAG = "DownloadService";

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
                .execute(new FileCallback(Environment.getExternalStorageDirectory()+BaseConfig.ROOT_DIR+"download/", title) {

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
                        String downloadLength = Formatter.formatFileSize(App.mInstance, progress.currentSize);
                        String totalLength = Formatter.formatFileSize(App.mInstance, progress.totalSize);
                        //已下载/总容量
                        LogUtil.d(TAG, String.format("%s/%s",downloadLength, totalLength));

                        //下载速度
                        String speed = Formatter.formatFileSize(App.mInstance, progress.speed);
                        LogUtil.d(TAG, String.format("%s/s",speed));

                        LogUtil.d(TAG, numberFormat.format(progress.fraction*100));

                        //Intent intent = new Intent();
                        //intent.putExtra("progress", (progress.fraction*100));
                        //sendBroadcast(intent);
                    }
                });
    }
}
