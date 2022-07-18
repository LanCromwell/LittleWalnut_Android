package cn.baby.love.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mobstat.StatService;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.message.PushAgent;
//import com.youzan.androidsdkx5.YouzanBrowser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.adapter.MainPagerAdapter;
import cn.baby.love.common.api.LocationSetRequest;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.AppUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.PreferUtil;
import cn.baby.love.common.utils.UpgradeUtil;
import cn.baby.love.common.view.NoScrollViewPager;
import cn.baby.love.common.view.dialog.VipPop;
import cn.baby.love.common.view.swipebacklayout.SwipeBackLayout;
import cn.baby.love.fragment.ExpertFragment;
import cn.baby.love.fragment.TodayFragment;
import cn.baby.love.fragment.WoDeFragment;
import cn.baby.love.fragment.youzan.YouzanFragment;
import cn.baby.love.fragment.youzan.YouzanKefuFragment;
import cn.baby.love.service.BabyService;
import cn.baby.love.service.ServiceUtils;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";
    @BindView(R.id.todayLy)
    LinearLayout todayLy;
    @BindView(R.id.expertyLy)
    LinearLayout expertyLy;
    @BindView(R.id.myLy)
    LinearLayout myLy;
    @BindView(R.id.bottomLy)
    LinearLayout bottomLy;
    @BindView(R.id.todayIcon)
    ImageView todayIcon;
    @BindView(R.id.todayText)
    TextView todayText;
    @BindView(R.id.expertIcon)
    ImageView expertIcon;
    @BindView(R.id.expertText)
    TextView expertText;
    @BindView(R.id.myIcon)
    ImageView myIcon;
    @BindView(R.id.myText)
    TextView myText;

    @BindView(R.id.line)
    RelativeLayout line;

    @BindView(R.id.rootLy)
    RelativeLayout rootLy;
    @BindView(R.id.guideIv4)
    ImageView guideIv4;
    @BindView(R.id.guideIv3)
    ImageView guideIv3;
    @BindView(R.id.guideIv2)
    ImageView guideIv2;
    @BindView(R.id.guideIv1)
    ImageView guideIv1;

    private SwipeBackLayout mSwipeBackLayout;
    private List<Fragment> fragments = new ArrayList<>();


    private TodayFragment mTodayFragment;
    @BindView(R.id.viewpager)
    NoScrollViewPager mViewPager;
    private VipPop mVipPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        initGuideIv();
        //禁用Activity右滑动关闭页面
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEnableGesture(false);
        initBottomNav();
        UpgradeUtil.checkVersion(this);
        ServiceUtils.startService(this, BabyService.class);
        requestWritePermissions();
        initBaidu();
        initYm();
    }

    //设置友盟alias
    private void initYm() {
        if (UserUtil.isLogin()) {
            PushAgent mPushAgent = PushAgent.getInstance(this);
            //添加别名
            mPushAgent.addAlias("xht" + UserUtil.getUserId(), "xht_app",
                    (isSuccess, message) -> LogUtil.i("友盟推送设置Alias，" + isSuccess + "," + message));
            //设置用户id和device_token的一一映射关系，确保同一个alias只对应一台设备：
            mPushAgent.setAlias("xht" + UserUtil.getUserId(), "xht_app",
                    (isSuccess, message) -> LogUtil.i("友盟推送绑定uuid和device_token的一一映射关系，" + isSuccess + "," + message));
        }
    }

    /**
     * 初始化引导页
     */
    private void initGuideIv() {
        boolean isShow = (boolean) PreferUtil.getInstance(this).getObject(PreferUtil.KEY_IS_OPEN_GUIDE_PAGE_FOR_MAIN, true);
        if (isShow) {
            PreferUtil.getInstance(this).putObject(PreferUtil.KEY_IS_OPEN_GUIDE_PAGE_FOR_MAIN, false);
            guideIv1.setVisibility(View.VISIBLE);
            guideIv2.setVisibility(View.VISIBLE);
            guideIv3.setVisibility(View.VISIBLE);
            guideIv4.setVisibility(View.VISIBLE);
        }
    }

    private void initBaidu() {
        StatService.start(this);
        UserBean userBean = UserUtil.getUserInfo();
        if (UserUtil.isLogin() && !TextUtils.isEmpty(userBean.channel_name)) {
            StatService.setAppChannel(this, userBean.channel_name, true);
        } else {
            StatService.setAppChannel(this, AppUtil.getUMChannelValue(this), true);
        }

        initLocationOption();
    }

    /**
     * 初始化底部导航组件；支持消息提醒，事件拦截，中间位置自定义View/加号
     */
    private void initBottomNav() {
        mTodayFragment = new TodayFragment();
        fragments.add(mTodayFragment);
        fragments.add(new ExpertFragment());//专家严选
//        fragments.add(new YouzanFragment());//有赞商城
//        fragments.add(new YouzanKefuFragment());//有赞客服
        fragments.add(new WoDeFragment());
        mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    rootLy.setBackgroundResource(R.drawable.toady_bg);
                } else if (position == 1 || position == 2) {
                    rootLy.setBackgroundResource(R.color.white);
                } else {
                    rootLy.setBackgroundResource(R.drawable.bg_header);
                }

                if (position == 1) {
                    showVipDialog();
                }

                if (position == 0) {
                    bottomLy.setBackgroundColor(getResources().getColor(R.color.login_btn_color));
                    line.setVisibility(View.GONE);
                    todayIcon.setImageResource(R.mipmap.tab_today_white);
                    expertIcon.setImageResource(R.mipmap.tab_shop_white);
                    myIcon.setImageResource(R.mipmap.tab_wode_white);

                    todayText.setTextColor(getResources().getColor(R.color.white));
                    expertText.setTextColor(getResources().getColor(R.color.white));
                    myText.setTextColor(getResources().getColor(R.color.white));
                    mTodayFragment.onResume();
                } else if (position == 1) {
                    bottomLy.setBackgroundColor(getResources().getColor(R.color.white));
                    line.setVisibility(View.VISIBLE);
                    todayIcon.setImageResource(R.mipmap.tab_today_black);
                    expertIcon.setImageResource(R.mipmap.tab_shop_yellow);
                    myIcon.setImageResource(R.mipmap.icon_wode_black);

                    todayText.setTextColor(getResources().getColor(R.color.color_555));
                    expertText.setTextColor(getResources().getColor(R.color.login_btn_color));
                    myText.setTextColor(getResources().getColor(R.color.color_555));
                } else if (position == 2) {
                    bottomLy.setBackgroundColor(getResources().getColor(R.color.white));
                    line.setVisibility(View.VISIBLE);
                    todayIcon.setImageResource(R.mipmap.tab_today_black);
                    expertIcon.setImageResource(R.mipmap.tab_shop_black);
                    myIcon.setImageResource(R.mipmap.tab_wode_yellow);

                    todayText.setTextColor(getResources().getColor(R.color.color_555));
                    expertText.setTextColor(getResources().getColor(R.color.color_555));
                    myText.setTextColor(getResources().getColor(R.color.login_btn_color));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pushMsg();
    }

    /**
     * 消息推送类型
     */
    private void pushMsg() {
        if (!TextUtils.isEmpty(App.pushType) && "1".equals(App.pushType)) {//推送类型为客服消息
            App.pushType = null;
            mViewPager.setCurrentItem(2, false);
        }
    }

    /**
     * 切换tab
     *
     * @param index
     */
    public void changeBottomTab(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 2) {
//            YouzanFragment youzanFragment = (YouzanFragment) fragments.get(1);
//            YouzanKefuFragment kefuFragment = (YouzanKefuFragment) fragments.get(2);
//            YouzanBrowser youzanBrowser;
//            if(mViewPager.getCurrentItem() == 1){
//                youzanBrowser = youzanFragment.youzanBrowser;
//            }else{
//                youzanBrowser = kefuFragment.youzanBrowser;
//            }
//
//            if(null != youzanBrowser &&youzanBrowser.pageCanGoBack()){
//                youzanBrowser.pageGoBack();
//                return false;
//            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent backHome = new Intent(Intent.ACTION_MAIN);
            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backHome.addCategory(Intent.CATEGORY_HOME);
            startActivity(backHome);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //vip的弹窗
    private void showVipDialog() {
        UserBean userBean = UserUtil.getUserInfo();
        if (userBean.is_receive_vip != 0) {//已经领取过VIP
            return;
        }
        //关闭弹窗
        mVipPop = new VipPop(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//关闭弹窗
                mVipPop.dismiss();
                mViewPager.setCurrentItem(0, false);
            }
        });
        mVipPop.showAtLocation(getRootView(), Gravity.TOP
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 获得rootview
     *
     * @return
     */
    public View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content))
                .getChildAt(0);
    }

    @Override
    public void hasPermissions() {
        super.hasPermissions();
    }

    @Override
    public void mainHasPermission() {
        super.mainHasPermission();
        mTodayFragment.hasPermission();
    }

    /**
     * 初始化百度定位参数配置
     */
    private void initLocationOption() {
        UserBean userBean = UserUtil.getUserInfo();
        if (null != userBean && userBean.is_set_location == 1) {//已经设置过了，不在设置
            return;
        }
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    @OnClick({R.id.todayLy, R.id.expertyLy, R.id.myLy, R.id.guideIv4, R.id.guideIv3, R.id.guideIv2, R.id.guideIv1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.todayLy:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.expertyLy:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.myLy:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.guideIv4:
                guideIv4.setVisibility(View.GONE);
                break;
            case R.id.guideIv3:
                guideIv3.setVisibility(View.GONE);
                break;
            case R.id.guideIv2:
                guideIv2.setVisibility(View.GONE);
                break;
            case R.id.guideIv1:
                guideIv1.setVisibility(View.GONE);
                break;


        }
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();


            UserBean userBean = UserUtil.getUserInfo();
            if (userBean.is_set_location == 0) {
                Address mAddress = location.getAddress();
                if (null != mAddress) {
                    String addStr = mAddress.address;
                    String proStr = mAddress.province;
                    String cityStr = mAddress.city;
                    String townStr = mAddress.district;
                    if (!TextUtils.isEmpty(proStr) && !TextUtils.isEmpty(cityStr) && !TextUtils.isEmpty(townStr)) {
                        LocationSetRequest.getInstance()
                                .setLocationInfo(proStr, cityStr, townStr, addStr);
                    }
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        App.pushType = null;
        super.onDestroy();
    }
}
