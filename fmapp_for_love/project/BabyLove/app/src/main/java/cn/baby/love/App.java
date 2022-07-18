package cn.baby.love;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.baidu.mobstat.StatService;
import com.fm.openinstall.OpenInstall;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.AppUtil;
import cn.baby.love.common.utils.CrashHandlerUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.PreferUtil;
import okhttp3.OkHttpClient;


/**
 * Created by wangguangbin on 2018/11/5.
 */
public class App extends Application {

    private static ArrayList<Activity> activities = new ArrayList<>();
    public static App mInstance;
    /**
     * 海报分享地址
     */
    public static String sharePicUrl;

    /**
     * 推送类型
     */
    public static String pushType;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //初始化全局crash
        CrashHandlerUtil.getInstance().init(getApplicationContext());
        //初始化sqlite(这里S使用litepal框架)
        LitePal.initialize(this);
        StatService.autoTrace(this);
        //初始化友盟
        initUment();
        //初始化OkGo
        initOkGo();
        initOpenInstall();
    }

    private void initOpenInstall(){
        if (isMainProcess()) {
            OpenInstall.init(this);
        }
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /**
         * 分割 Dex 支持
         * @param base
         */
        MultiDex.install(this);
    }
    //static 代码段可以防止内存泄露
    //https://github.com/scwang90/SmartRefreshLayout
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.black);//全局设置主题颜色
                ClassicsHeader mRefreshHeader = new ClassicsHeader(context);
                mRefreshHeader.setEnableLastTime(false);
                return mRefreshHeader;//
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.black);//全局设置主题颜色
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    /**
     * 友盟统计
     */
    private void initUment() {
        initUmentPush();
        //友盟分享   https://developer.umeng.com/docs/66632/detail/66639#h2-u624Bu52A8u96C6u62106
        PlatformConfig.setWeixin("wxccf50f4c57ae406d", "c104accc32cf5d1e0b61ebd7b6a8e4c4");
//        PlatformConfig.setSinaWeibo("1158761449", "24e5369e18c0963be5cb49a4192a40da","http://sns.whalecloud.com");
        PlatformConfig.setQQZone("101547696", "24af30ae1b18c0dd48424ccecb976f47");
    }

    /**
     * 关闭友盟推送
     */
    public void closeUmengPush(){
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.disable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                LogUtil.i("推送关闭");
            }

            @Override
            public void onFailure(String s, String s1) {}
        });
    }

    /**
     * 打开友盟推送
     */
    public void openUmengPush(){
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
//                LogUtil.i("推送打开");
            }

            @Override
            public void onFailure(String s, String s1) {}
        });
    }

    /**
     * 初始化友盟推送
     */
    public void initUmentPush() {
        String channelStr = AppUtil.getUMChannelValue(this);
        try {
            if(UserUtil.isLogin()){
                channelStr = UserUtil.getUserInfo().channel_name;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //友盟统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //友盟推送
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey（需替换）；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        UMConfigure.init(this, "5c2d7732b465f529e200005a", channelStr, UMConfigure.DEVICE_TYPE_PHONE,
                "3c33c97440c24c79693a1c34b9d91e89");
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDisplayNotificationNumber(5);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                LogUtil.i("注册成功：deviceToken：-------->  " + deviceToken);
                boolean isAcceptPush = (Boolean) PreferUtil.getInstance(mInstance).getObject(PreferUtil.KEY_IS_OPEN_REMIND, true);
                if(isAcceptPush){
                    openUmengPush();
                }else{
                    closeUmengPush();
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.i("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setMessageHandler(messageHandler);

        MiPushRegistar.register(this, "2882303761517968152", "5181796822152");//小米推送通道初始化
        HuaWeiRegister.register(this);//华为推送通道
        MeizuRegister.register(this, "119517", "adf2b4c87d244dfab965cb9e055163c6");//魅族推送通道

    }

    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
        @Override
        public void launchApp(Context context, UMessage uMessage) {
            super.launchApp(context, uMessage);
            try{
                pushType = uMessage.extra.get("type");
                LogUtil.i("推送消息222 = "+uMessage.extra.get("type"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage msg) {
            boolean isAcceptPush = (Boolean) PreferUtil.getInstance(mInstance).getObject(PreferUtil.KEY_IS_OPEN_REMIND, true);
            LogUtil.i("推送消息111 = "+msg.extra);
            if(isAcceptPush){
                //默认为0，若填写的builder_id并不存在，也使用默认。
                return super.getNotification(context, msg);
            }else{

            }
            return null;
        }
    };

    /**
     * 初始化OkGo网络框架
     */
    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));     //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //方法二：...

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(null)                         //全局公共头
                .addCommonParams(null);                         //全局公共参数
    }
}
