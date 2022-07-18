package cn.baby.love.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import cn.baby.love.R;

/**
 * @author wangxin
 * @date 2018-11-6
 * @desc App信息获取工具类
 */
public class AppUtil {

    /**
     * 获取当前app版本名称（versionName）
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前app版本号（versionCode）
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;

    }

    /**
     * 获取当前手机系统版本号
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取手机型号
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备id Android ID
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String drivenToken = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (drivenToken == null) {
            drivenToken = android.provider.Settings.System.getString(context.getContentResolver(),
                    android.provider.Settings.System.ANDROID_ID);
            if (drivenToken == null) {
                return "";
            }
        }
        return drivenToken;
    }

    /**
     * 读取Manifest文件下Application 等节点下的meta-data自定义数据
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getApplicationMetaData(Context context, String key, String defaultValue){
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key, defaultValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取友盟渠道标识
     * @param context
     * @return
     */
    public static String getUMChannelValue(Context context){
        return getApplicationMetaData(context, "UMENG_CHANNEL", "aa");
    }

    /**
     * 获取 SDCard 总容量大小
     * @return MB
     */
    public static long getSDCardTotalSize() {
        //得到外部储存sdcard的状态
        String sdcard = Environment.getExternalStorageState();
        //外部储存sdcard存在的情况
        String state = Environment.MEDIA_MOUNTED;
        //获取Sdcard的路径
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        if(sdcard.equals(state)){
            //获得sdcard上 block的总数
            long blockCount=statFs.getBlockCount();
            //获得sdcard上每个block 的大小
            long blockSize=statFs.getBlockSize();
            //计算标准大小使用：1024，当然使用1000也可以
            long bookTotalSize=blockCount*blockSize/1024/1024;
            return bookTotalSize;
        }else{
            return -1;
        }
    }

    /**
     * 计算Sdcard的剩余大小
     * @return MB
     */
    public static long getSDCardAvailableSize() {
        //得到外部储存sdcard的状态
        String sdcard = Environment.getExternalStorageState();
        //外部储存sdcard存在的情况
        String state = Environment.MEDIA_MOUNTED;
        //获取Sdcard的路径
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        if(sdcard.equals(state)){
            //获得Sdcard上每个block的size
            long blockSize=statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable=statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal=blockSize*blockavailable/1024/1024;
            return blockavailableTotal;
        }else{
            return -1;
        }
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取当前的运营商名称
     * @param context
     * @return 运营商名字
     */
    public static String getOperatorName(Context context){
        try{
            String ProvidersName = "";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String IMSI = telephonyManager.getSubscriberId();
            if (IMSI != null) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    ProvidersName = "中国移动";
                } else if (IMSI.startsWith("46001")  || IMSI.startsWith("46006")) {
                    ProvidersName = "中国联通";
                } else if (IMSI.startsWith("46003")) {
                    ProvidersName = "中国电信";
                }
                return ProvidersName;
            } else {
                return "no_sim_info";
            }
        }catch(Exception e){
            return "no_sim_info";
        }
    }

    //----------------------------------------------------------------------------------------------------------------------

    /**
     * 获取相关信息
     * @param context
     * @return
     */
    public static JSONArray getInfo(Context context){
        JSONArray list = new JSONArray();
        list.put(joinInfo(0, "设备号", AppUtil.getIMEI(context)));
        list.put(joinInfo(0, "手机厂商", AppUtil.getDeviceBrand()));
        list.put(joinInfo(0, "手机型号", AppUtil.getSystemModel()));
        list.put(joinInfo(0, "系统语言", AppUtil.getSystemLanguage()));
        list.put(joinInfo(0, "操作系统", "Android"+AppUtil.getSystemVersion()));
        list.put(joinInfo(0, "网络类型", NetworkUtil.isWifi(context)?"wifi":"mobile"));
        list.put(joinInfo(0, "运营商", AppUtil.getOperatorName(context)));
        list.put(joinInfo(0, "平台类型", AppUtil.isTablet(context)?"AndroidTablet":"AndroidMobile"));
        list.put(joinInfo(0, "App名称", context.getString(R.string.app_name)));
        list.put(joinInfo(0, "App版本", AppUtil.getAppVersionName(context)));
        return list;
    }

    /**
     * @param itemKey
     * @param itemValue
     * @return
     */
    private static JSONObject joinInfo(int category, String itemKey, String itemValue){
        JSONObject item = new JSONObject();
        try{
            item.put("itemKey", itemKey);
            item.put("itemValue", itemValue);
            return item;
        }catch(Exception e){
            return item;
        }
    }

}
