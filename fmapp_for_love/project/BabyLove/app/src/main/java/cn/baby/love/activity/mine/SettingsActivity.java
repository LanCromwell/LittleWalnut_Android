package cn.baby.love.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;
import cn.baby.love.activity.LoginActivity;
import cn.baby.love.activity.MainActivity;
import cn.baby.love.adapter.MainPagerAdapter;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.bean.LanguageInfo;
import cn.baby.love.common.bean.RoleInfo;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.PreferUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.NoScrollViewPager;
import cn.baby.love.fragment.ExpertFragment;
import cn.baby.love.fragment.TodayFragment;
import cn.baby.love.fragment.WoDeFragment;
import cn.baby.love.fragment.setting.SettingsOne;
import cn.baby.love.fragment.setting.SettingsThree;
import cn.baby.love.fragment.setting.SettingsTwo;
/**
 * 重新设置
 */
public class SettingsActivity extends BaseActivity {

    @BindView(R.id.leftIconLy)
    RelativeLayout leftIconLy;
    @BindView(R.id.centerTv)
    TextView centerTv;
    @BindView(R.id.rightTv1)
    TextView rightTv1;
    @BindView(R.id.rightLayout)
    RelativeLayout rightLayout;
    @BindView(R.id.shenfenIv)
    ImageView shenfenIv;
    @BindView(R.id.shenfenTv)
    TextView shenfenTv;
    @BindView(R.id.icon_right_line)
    ImageView iconRightLine;
    @BindView(R.id.luaIv)
    ImageView luaIv;
    @BindView(R.id.luaTv)
    TextView luaTv;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;

    private List<Fragment> fragments = new ArrayList<>();
    private SettingsOne mSettingsOne;
    private SettingsThree mSettingsThree;
    private SettingsTwo mSettingsTwo;
    //是否要进入登录页面
    private boolean isNeedGoMainActivity = false;
    public static final String KEY_PARAMS = "key_params_from";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initTitle("设置");
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (null != intent) {
            isNeedGoMainActivity = intent.getBooleanExtra(KEY_PARAMS, false);
        }

        rightTv1.setVisibility(View.VISIBLE);
        rightTv1.setText("下一步");
        rightTv1.setTextColor(Color.WHITE);
        rightTv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);

        mSettingsOne = new SettingsOne();
        mSettingsTwo = new SettingsTwo();
        mSettingsThree = new SettingsThree();
        fragments.add(mSettingsOne);
        fragments.add(mSettingsTwo);
        fragments.add(mSettingsThree);
        viewpager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(3);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    shenfenIv.setImageResource(R.drawable.icon_shenfen_no_select);
                    shenfenTv.setTextColor(getResources().getColor(R.color.color_d8));

                    luaIv.setImageResource(R.drawable.icon_lua_no_select);
                    luaTv.setTextColor(getResources().getColor(R.color.color_d8));

                    iconRightLine.setImageResource(R.drawable.icon_hor_line_no_selected);
                    rightTv1.setText("下一步");

                }else if(position == 1){
                    shenfenIv.setImageResource(R.drawable.icon_shenfen_selected);
                    shenfenTv.setTextColor(getResources().getColor(R.color.gray_999));

                    luaIv.setImageResource(R.drawable.icon_lua_no_select);
                    luaTv.setTextColor(getResources().getColor(R.color.color_d8));

                    iconRightLine.setImageResource(R.drawable.icon_hor_line_no_selected);
                    rightTv1.setText("下一步");
                }else{
                    shenfenIv.setImageResource(R.drawable.icon_shenfen_selected);
                    shenfenTv.setTextColor(getResources().getColor(R.color.gray_999));

                    luaIv.setImageResource(R.drawable.icon_lua_seleced);
                    luaTv.setTextColor(getResources().getColor(R.color.gray_999));

                    iconRightLine.setImageResource(R.drawable.icon_hor_line_selected);
                    rightTv1.setText("完成");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isNeedGoMainActivity) {
            UserUtil.saveUserInfo(null);
        }
        super.onBackPressed();
    }

    public void setCurrentPage(int index){
        viewpager.setCurrentItem(index);
    }

    @OnClick({R.id.rightLayout, R.id.birthdayLy, R.id.shenfenLy, R.id.luaLy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rightLayout:
                if(viewpager.getCurrentItem() == 0){
                    if(!mSettingsOne.isSettings()){
                        ToastUtil.showToast(SettingsActivity.this,"请选择宝宝出生日");
                        return;
                    }
                }else if(viewpager.getCurrentItem() == 1){
                    if(!mSettingsTwo.isSettings()){
                        ToastUtil.showToast(SettingsActivity.this,"请选择身份");
                        return;
                    }
                }else if(viewpager.getCurrentItem() == 2){
                    if(!mSettingsThree.isSettings()){
                        ToastUtil.showToast(SettingsActivity.this,"请设定语言");
                        return;
                    }
                }

                if (viewpager.getCurrentItem() == 2) {
                    // 完成
                    saveUserInfo();
                } else {
                    viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
                }
                break;
            case R.id.birthdayLy:
                viewpager.setCurrentItem(0);
                break;
            case R.id.shenfenLy:
                if(!mSettingsOne.isSettings()){
                    ToastUtil.showToast(SettingsActivity.this,"请选择宝宝出生日");
                    return;
                }
                viewpager.setCurrentItem(1);
                break;
            case R.id.luaLy:
                if(!mSettingsOne.isSettings()){
                    ToastUtil.showToast(SettingsActivity.this,"请选择宝宝出生日");
                    return;
                }
                if(!mSettingsTwo.isSettings()){
                    ToastUtil.showToast(SettingsActivity.this,"请选择身份");
                    return;
                }
                viewpager.setCurrentItem(2);
                break;
        }
    }

    public void saveUserInfo() {
        //保存当前信息选择的信息
        RoleInfo curRoleInfo = mSettingsTwo.getCurRoleInfo();
        LanguageInfo curLanguageInfo = mSettingsThree.getCurLanguageInfo();

        PreferUtil.getInstance(this).putObject(PreferUtil.KEY_CUR_LANGUAGE_INFOS, curLanguageInfo);
        PreferUtil.getInstance(this).putObject(PreferUtil.KEY_CUR_ROLE_INFOS, curRoleInfo);

        updateSettingInfo(curLanguageInfo.id, curRoleInfo.id, (null == mSettingsOne.getCurDate()) ? System.currentTimeMillis() / 1000 : mSettingsOne.getCurDate().getTime() / 1000);
    }

    /**
     * 更新用户信息
     *
     * @param language_id
     * @param role_id
     * @param child_birthday
     */
    private void updateSettingInfo(int language_id, int role_id, long child_birthday) {
        showLoading();
        Api.getInstance().updateUserInfo(language_id, role_id, child_birthday, new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                getUserInfo();
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                cancelLoading();
                ToastUtil.showToast(getApplicationContext(), R.string.net_error);
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                cancelLoading();
                ToastUtil.showToast(getApplicationContext(), "设置失败，请稍后重试");
            }
        });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(){
        UserInfoRequest.getInstance().getUserInfo(new UserInfoRequest.OnUserInfoRequestListener() {
            @Override
            public void OnUserInfoRequestListener_result(boolean isSuccess, String msg) {
                cancelLoading();
                if(isSuccess){
                    UserBean userBean = UserUtil.getUserInfo();
                    userBean.first_login = 0;
                    UserUtil.saveUserInfo(userBean);
                    sendBroadCast();
                    ToastUtil.showToast(SettingsActivity.this, "设置成功");

                    if (isNeedGoMainActivity) {
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    }
                    finish();
                }else{
                    ToastUtil.showToast(SettingsActivity.this, ""+msg);
                }
            }
        });
    }

    /**
     * 刷新今日提醒
     */
    private void sendBroadCast() {
        sendBroadcast(new Intent(BaseConfig.ACTION_REFRESH_TODAY_INFO));
    }

}
