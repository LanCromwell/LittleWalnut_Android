package cn.baby.love.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.WebActivity;
import cn.baby.love.activity.mine.InvitateCodeActivity;
import cn.baby.love.activity.mine.LikedActivity;
import cn.baby.love.activity.mine.SettingsActivity;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.base.BaseFragment;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.GlideApp;
import cn.baby.love.common.utils.StrUtil;
import cn.baby.love.common.view.dialog.CustomDialog;

public class WoDeFragment extends BaseFragment {

    @BindView(R.id.userIcon)
    QMUIRadiusImageView userIcon;
    @BindView(R.id.nickName)
    TextView nickName;
    @BindView(R.id.hasLearnDays)
    TextView hasLearnDays;
    private Unbinder mUnbinder;

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wode, null);
        mUnbinder = ButterKnife.bind(this, v);

        initView();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(),"我的");
    }


    private void initView() {
        initUserInfo();
//        tixingCb.setChecked((Boolean) PreferUtil.getInstance(getContext()).getObject(PreferUtil.KEY_IS_OPEN_REMIND, true));
//        tixingCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                PreferUtil.getInstance(getContext()).putObject(PreferUtil.KEY_IS_OPEN_REMIND, isChecked);
//                if (isChecked) {//打开友盟
//                    showTixingDialog();
//                    App.mInstance.openUmengPush();
//                }else{//关闭友盟
//                    App.mInstance.closeUmengPush();
//                }
//            }
//        });
    }

    private void initUserInfo() {
        updateUserInfo();
        if(UserUtil.isLogin()){
            UserInfoRequest.getInstance().getUserInfo(new UserInfoRequest.OnUserInfoRequestListener() {
                @Override
                public void OnUserInfoRequestListener_result(boolean isSuccess, String msg) {
                    if(isSuccess){
                        updateUserInfo();
                    }
                }
            });
        }
    }

    private void updateUserInfo(){
        if (UserUtil.isLogin()) {
            UserBean mUserInfo = UserUtil.getUserInfo();
            GlideApp.with(this)
                    .load(mUserInfo.avatar)
                    .placeholder(R.drawable.icon_touxiang_moren)
                    .error(R.drawable.icon_touxiang_moren)
                    .into(userIcon);
            nickName.setText(StrUtil.parseEmpty(mUserInfo.role_name));
            if(1 == mUserInfo.is_free){
                hasLearnDays.setText(StrUtil.parseEmpty(mUserInfo.register_date));
            }else{
                hasLearnDays.setText("试用期倒数"+mUserInfo.remainder_days+"天");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(),"我的");
        initUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R.id.userIcon, R.id.collectTv, R.id.settingTv, R.id.feedbackLayout, R.id.juanzhuTv, R.id.shareTv,R.id.yaoqingmaTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userIcon:
                break;
            case R.id.collectTv:
                Intent intent = new Intent(getContext(), LikedActivity.class);
                intent.putExtra(LikedActivity.KEY_PARAM_TITLE,"我的收藏");
                intent.putExtra(LikedActivity.KEY_PARAM_TYPE,0);
                startActivity(intent);
                break;
            case R.id.settingTv:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
            case R.id.feedbackLayout:
//                startActivity(new Intent(getContext(), FeedBackActivity.class));
                intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_KEY, new WebInfo(getActivity().getString(R.string.user_fuwu_yinsi), "http://www.mamaucan.com.cn/law"));
                startActivity(intent);
                break;
            case R.id.juanzhuTv:
                Intent intent1 = new Intent(getActivity(),WebActivity.class);
                intent1.putExtra(WebActivity.EXTRA_KEY,new WebInfo("鸣谢名单","http://www.mamaucan.com.cn/thanks"));
                startActivity(intent1);
                break;
            case R.id.shareTv:
                Intent intent2 = new Intent(getActivity(),WebActivity.class);//积分商城
                intent2.putExtra(WebActivity.EXTRA_KEY,new WebInfo("版权信息","http://www.mamaucan.com.cn/club"));
                startActivity(intent2);
                break;
            case R.id.yaoqingmaTv:
                startActivity(new Intent(getContext(), InvitateCodeActivity.class));
                break;
        }
    }

    private void showTixingDialog() {
        dialog = CustomDialog.confirmOneBtn(getContext(), "今日育儿提醒不错过", "确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @OnClick(R.id.logOutBtn)
    public void onViewClicked() {
        //退出登录
        UserUtil.logout(getActivity());
    }
}
