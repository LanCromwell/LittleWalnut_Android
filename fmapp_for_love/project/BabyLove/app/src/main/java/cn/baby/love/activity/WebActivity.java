package cn.baby.love.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.baby.love.R;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.utils.NetworkUtil;
import cn.baby.love.common.utils.ToastUtil;

/**
 * @author wangxin
 * @date 2018-11-6
 * @desc 网页浏览Activity
 */
public class WebActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    public final static String EXTRA_KEY = "extra_key_webinfo";
    private DownloadManager downloadManager;
    private WebInfo mWebInfo;

    @Override
    public boolean isSettingTranslucentStatusBar() {
        return false;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        mWebInfo = (WebInfo)getIntent().getSerializableExtra(EXTRA_KEY);
        if(mWebInfo == null){
            //TODO 需提供错误提示
            mWebInfo = new WebInfo("错误提示", "错误提示页");
        }

        initTitle(mWebInfo.getTitle());

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        tipDialog = new QMUITipDialog.Builder(this)
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                .setTipWord("正在加载")
//                .create();

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setDefaultTextEncodingName("utf-8");
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAppCacheEnabled(true);
        s.setBlockNetworkImage(false);
        s.setAllowFileAccess(true);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setHorizontalScrollBarEnabled(false);
        webView.addJavascriptInterface(this, "app");
        webView.setDownloadListener(new MyDownloader());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        String url = mWebInfo.getUrl();
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            url = String.format("http://%s", url);
            mWebInfo.setUrl(url);
        }
        webView.loadUrl(url);
    }


    /**
     * 自定义WebViewClient使支持打开微信、支付宝
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            tipDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            tipDialog.dismiss();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url == null) return false;
            try {
                //自定义的scheme
                if(url.startsWith("weixin://") || url.startsWith("alipays://")
                        || url.startsWith("mailto://") || url.startsWith("tel://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if(url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb")){
                    Map<String,String> extraHeaders = new HashMap<>();
                    extraHeaders.put("Referer", "https://pay.boxuegu.com");
                    view.loadUrl(url, extraHeaders);
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }

            //处理http和https开头的url
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 自定义下载类
     */
    private class MyDownloader implements DownloadListener {

        @Override
        public void onDownloadStart(final String url, String userAgent, final String contentDisposition,
                                    final String mimetype, long contentLength) {

            if(!NetworkUtil.isAvailable(WebActivity.this)){
                ToastUtil.showToast(WebActivity.this, "网络好像有点问题，请检查后重试！");
                return ;
            }
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                ToastUtil.showToast(WebActivity.this, "您的存储卡有问题，暂时不能下载附件！");
                return ;
            }
            if(NetworkUtil.isWifi(WebActivity.this)){
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        osServerDownload(url, contentDisposition, mimetype);
                    } else {
                        toBrowserDownload(url);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
//                new QMUIDialog.MessageDialogBuilder(WebActivity.this)
//                        .setTitle("提醒")
//                        .setMessage("是否允许在移动网络下下载该文件？")
//                        .addAction("取消", new QMUIDialogAction.ActionListener() {
//                            @Override
//                            public void onClick(QMUIDialog dialog, int index) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .addAction("确定", new QMUIDialogAction.ActionListener() {
//                            @Override
//                            public void onClick(QMUIDialog dialog, int index) {
//                                dialog.dismiss();
//                                try {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                        osServerDownload(url, contentDisposition, mimetype);
//                                    } else {
//                                        toBrowserDownload(url);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        })
//                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        }

        /**
         * 使用系统的下载服务
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void osServerDownload(String url, String contentDisposition, String mimeType){
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            // 指定下载地址
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            //设置下载文件类型;这是安卓.apk文件的类型。有些机型必须设置此方法，才能在下载完成后，点击通知栏的Notification时，才能正确的打开安装界面。不然会弹出一个Toast（can not open file）
            request.setMimeType("application/vnd.android.package-archive");
            // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
            request.allowScanningByMediaScanner();
            // 设置通知的显示类型，下载进行时和完成后显示通知
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 设置通知栏的标题，如果不设置，默认使用文件名
            request.setTitle(fileName);
            // 设置通知栏的描述
            //request.setDescription("博学谷-问答详情附件下载");
            // 允许在计费流量下下载
            request.setAllowedOverMetered(true);
            // 允许该记录在下载管理界面可见
            request.setVisibleInDownloadsUi(true);
            // 允许漫游时下载
            request.setAllowedOverRoaming(true);
            // 允许下载的网路类型
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            //自定义下载路径
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            // 添加一个下载任务
            downloadManager.enqueue(request);
        }

        /**
         * 跳转浏览器下载
         */
        private void toBrowserDownload(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_APP_BROWSER);
            startActivity(intent);
        }
    }

    private void doFinish(){
        if(webView.canGoBack()) {
            webView.goBack();
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }
}
