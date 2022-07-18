package cn.baby.love.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.baby.love.App;
import cn.baby.love.R;
import cn.baby.love.adapter.WelcomAdpater;
import cn.baby.love.common.api.RoleLanguageRequest;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.PreferUtil;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * 使用小米或者华为的通道需继承UmengNotifyClickActivity
 */
public class WelcomeActivity extends UmengNotifyClickActivity {

    private ViewPager viewPager;
    private int count = 3;
    List<Integer> imgList = new ArrayList<>();
    private LinearLayout tiyanBtn;
    private RelativeLayout splashLayout;
    private RelativeLayout guideLayout;
    private ImageView jumpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //兼容小米首次安装（点击小米通道消息）并直接启动应用，每次home后点击应用图标重新启动Bug
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        setContentView(R.layout.activity_welcome);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tiyanBtn = (LinearLayout) findViewById(R.id.tiyanBtn);
        splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);
        guideLayout = (RelativeLayout) findViewById(R.id.guideLayout);
        jumpBtn = (ImageView) findViewById(R.id.jumpBtn);
        handler.sendEmptyMessageDelayed(123, 1000);

        RoleLanguageRequest.getInstance().getLaugage(null);
        RoleLanguageRequest.getInstance().getRoleInfos(null);
        tiyanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity();
            }
        });
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity();
            }
        });
        if (UserUtil.isLogin()) {
            UserInfoRequest.getInstance().getUserInfo(null);
        }

        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };

    //push消息通知
    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        try {
            if (null != intent) {
                String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
                LogUtil.i("消息来了 = " + body);
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    JSONObject extraJson = jsonObject.optJSONObject("extra");
                    String pushType = extraJson.optString("type");
                    App.pushType = pushType;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void goActivity() {
        if (UserUtil.isLogin() && null != UserUtil.getUserInfo().youzan_info && !TextUtils.isEmpty(UserUtil.getUserInfo().youzan_info
                .shop_href)) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

        } else {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));

        }
        finish();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123) {
                count--;
                if (count > 0) {
                    handler.sendEmptyMessageDelayed(123, 1000);
                } else {
                    startGuidePage();
                }
            }
        }
    };

    private void startGuidePage() {
        boolean isShow = (boolean) PreferUtil.getInstance(this).getObject(PreferUtil.KEY_IS_OPEN_GUIDE_PAGE, true);
        if (isShow) {
            PreferUtil.getInstance(this).putObject(PreferUtil.KEY_IS_OPEN_GUIDE_PAGE, false);
            initViewPager();
        } else {
            goActivity();
        }
    }

    private void initViewPager() {
        splashLayout.setVisibility(View.GONE);
        guideLayout.setVisibility(View.VISIBLE);
//        imgList.add(R.drawable.bg_welcome_one);
//        imgList.add(R.drawable.bg_welcome_two);
        imgList.add(R.drawable.bg_welcome_three);

        WelcomAdpater adpater = new WelcomAdpater(this, imgList);
        viewPager.setAdapter(adpater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(123);
        wakeUpAdapter = null;
    }

}
