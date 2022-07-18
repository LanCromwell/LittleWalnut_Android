package cn.baby.love.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.baby.love.R;
import cn.baby.love.activity.MainActivity;
import cn.baby.love.activity.WebActivity;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.CollectRequest;
import cn.baby.love.common.api.UserInfoRequest;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.base.BaseFragment;
import cn.baby.love.common.bean.TodayInfo;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.bean.WebInfo;
import cn.baby.love.common.manager.MediaPlayerManager;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.PreferUtil;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.JmRecyclerView;
import cn.baby.love.common.view.calendar.CustomMonthView;
import cn.baby.love.common.view.dialog.HistoryListPop;
import cn.baby.love.common.view.dialog.TryDayDialog;
import cn.baby.love.common.view.share.ShareBean;
import cn.baby.love.common.view.share.ShareDialog;

public class TodayFragment extends BaseFragment implements CalendarView.OnCalendarSelectListener
        , SeekBar.OnSeekBarChangeListener, MediaPlayerManager.OnPlayerListener, JmRecyclerView.OnRefreshListener {

    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @BindView(R.id.timeTv)
    TextView timeTv;

    @BindView(R.id.bottomLayout)
    RelativeLayout bottomLayout;
    @BindView(R.id.bottomLayout2)
    RelativeLayout bottomLayout2;

    @BindView(R.id.playBtn)
    ImageView playBtn;
    @BindView(R.id.likeBtn)
    ImageView likeBtn;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.curSeekTv)
    TextView curSeekTv;
    @BindView(R.id.babyAge)
    TextView babyAge;
    @BindView(R.id.totalSeekTv)
    TextView totalSeekTv;
    @BindView(R.id.shareBtn)
    ImageView shareBtn;
    @BindView(R.id.centerLayout)
    LinearLayout centerLayout;
    @BindView(R.id.mJmRecyclerView)
    JmRecyclerView mJmRecyclerView;
    private Unbinder unbinder;

    private Handler mHandler = new Handler();
    private MediaPlayerManager playerManager;
    private CollectRequest mCollectRequest;

    /**
     * 当前天信息
     */
    private TodayInfo mTodayInfo;
    private List<TodayInfo> todayInfoList = new ArrayList<>();
    /**
     * 已学习过的集合
     */
    private Map<String, Calendar> map = new HashMap<>();
    private int fragmentIndex = 0;
    private TryDayDialog mTryDayDialog;
    private boolean isShowTryDialog = false;
    private boolean isFirstInit = true;
    private boolean isFirstLoadInfos = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jingxuan, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mJmRecyclerView.setOnRefreshListener(true, false, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BaseConfig.ACTION_REFRESH_TODAY_INFO);
        filter.addAction(BaseConfig.ACTION_NOTIFY_OTHER_PLAYER_PAUSE);
        filter.addAction(BaseConfig.ACTION_NOTIFY_SHARE_SUCCESS);
        getActivity().registerReceiver(mBroadCastReceiver, filter);//监听首页刷新
        ((MainActivity) getActivity()).showLoading();
//        initCalendarViews();
        MobclickAgent.onEvent(getContext(), "today_remaind", "今日提醒");
    }

    BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BaseConfig.ACTION_NOTIFY_OTHER_PLAYER_PAUSE.equals(action)) {//暂停播放
                if (null != playerManager) {
                    playerManager.pause();
                }
            } else if (BaseConfig.ACTION_REFRESH_TODAY_INFO.equals(action)) {//刷新
                onRefresh();
            } else if (BaseConfig.ACTION_NOTIFY_SHARE_SUCCESS.equals(action)) {//分享成功
                getUserInfo();
            }
        }
    };

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        UserInfoRequest.getInstance().getUserInfo((isSuccess, msg) -> {
            if (isSuccess) {
                getActivity().runOnUiThread(() -> {
                    UserBean userBean = UserUtil.getUserInfo();
                    if (userBean.is_free == 1) {//终生免费
                        if (null != mTryDayDialog && mTryDayDialog.isShowing()) {
                            mTryDayDialog.cancel();
                        }
                    } else if (userBean.remainder_days > 0) {//试用期没有过
                        if (!isShowTryDialog) {//没有弹过试用期弹窗
                            isShowTryDialog = true;
                            showTrydayDialog();
                        } else if (null != mTryDayDialog && mTryDayDialog.isShowing()) {
                            mTryDayDialog.cancel();
                        }
                    } else {//试用期已过
                        showTrydayDialog();
                    }
                });

            }
        });
    }

    /**
     * 试用期倒数弹窗.
     */
    private void showTrydayDialog() {
        if (null != mTryDayDialog && mTryDayDialog.isShowing()) {
            return;
        }
        UserBean userBean = UserUtil.getUserInfo();
        TryDayDialog.Builder builder = new TryDayDialog.Builder(getContext());
        builder.setMessage("试用期倒数" + userBean.remainder_days + "天，转发赠送" + userBean.user_share_get_day + "天");
        builder.setNegativeButton((dialog, which) -> {
            ((MainActivity) getActivity()).mainActivityReqeusPermissions();
            if (isFirstLoadInfos) {
                getInfos();
            }
        });
        builder.setPositiveButton((dialog, which) -> {
            if (userBean.is_free != 1 && userBean.remainder_days <= 0) {//不是免费，并且已经试用期到期了
                //跳转到发现页面
                ((MainActivity) getActivity()).changeBottomTab(1);
            }
            if (isFirstLoadInfos) {
                getInfos();
            }
            mTryDayDialog.cancel();
        });
        mTryDayDialog = builder.create();
        mTryDayDialog.show();
    }

    @Override
    public void onRefresh() {//重新加载
        isFirstInit = true;
        getInfos();
    }

    @Override
    public void onLoadMore() {
    }

    /**
     * 获取当月信息
     */
    private void getInfos() {
        if (null != todayInfoList && todayInfoList.size() > 0 && null != playerManager && playerManager.isPlaying()) {
            return;
        }
        isFirstLoadInfos = false;
        Api.getInstance().getTodayInfo(new ApiCallback() {
            @Override
            public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
                ((MainActivity) getActivity()).cancelLoading();
                if (!isSuccess) {
                    mJmRecyclerView.showFail();
                    initCalendarViews();
                } else {
                    mJmRecyclerView.showNormal();
                    mJmRecyclerView.setVisibility(View.GONE);
                    TodayInfo todayInfo = ((MainActivity) getActivity()).gson.fromJson(result.toString(), TodayInfo.class);
                    if (null != todayInfo && null != todayInfo.data) {
                        todayInfoList.clear();
                        todayInfoList.addAll(todayInfo.data);
                    }
                    initCalendarViews();
                }
            }

            @Override
            public void onNotNetwork(String msg) {
                super.onNotNetwork(msg);
                ((MainActivity) getActivity()).cancelLoading();
                mJmRecyclerView.showNetErr();
                initCalendarViews();
            }

            @Override
            public void onFail(Response<String> response, String msg) {
                super.onFail(response, msg);
                ((MainActivity) getActivity()).cancelLoading();
                mJmRecyclerView.showFail();
                initCalendarViews();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onPageStart(getActivity(), "今日提醒");
        getUserInfo();
    }

    private void updateBabyAge() {
        if (null != babyAge) {
            if (UserUtil.isLogin()) {
                babyAge.setVisibility(View.VISIBLE);
                if (null != mTodayInfo && !TextUtils.isEmpty(mTodayInfo.title)) {
                    babyAge.setText(mTodayInfo.title);
                }
            } else {
                babyAge.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initCalendarViews() {
        seekBar.setOnSeekBarChangeListener(this);
        if (null == playerManager) {
            playerManager = new MediaPlayerManager(getActivity());
            playerManager.setOnPlayerListener(this);
            playerManager.initPlayer();
        }
        setCalendarView();
    }

    private void setCalendarView() {
        if (isFirstInit) {
            isFirstInit = false;
            mCalendarView.setSchemeColor(R.color.red, R.color.white, R.color.gray_eee);
            mCalendarView.setMonthView(CustomMonthView.class);
            mCalendarView.setMonthViewScrollable(false);
            mCalendarView.setWeeColor(Color.WHITE, R.color.gray_666);
            mCalendarView.setOnCalendarSelectListener(this);
            mCalendarView.setSchemeDate(map);

            if (ScreenUtil.getScreenHeight(getContext()) <= 1280) {//适配小屏幕
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) bottomLayout2.getLayoutParams();
                params2.setMargins(0, 0, 0, 0);
                bottomLayout2.setLayoutParams(params2);
            }
        }
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        initSelectMap();
    }

    private void initSelectMap() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        map.clear();
        for (TodayInfo todayInfo : todayInfoList) {
            if (1 == todayInfo.is_study) {
                map.put(getSchemeCalendar(year, month, todayInfo.day, 0xFF40db25, "").toString(),
                        getSchemeCalendar(year, month, todayInfo.day, 0xFF40db25, ""));
            }
        }
        mCalendarView.update();
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    private boolean isFristSelect = true;

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        TodayInfo tInfo = null;
        for (TodayInfo todayInfo : todayInfoList) {
            if (todayInfo.day == calendar.getDay()) {
                tInfo = todayInfo;
                break;
            }
        }
        timeTv.setText(calendar.getYear() + "年" + calendar.getMonth() + "月" + calendar.getDay() + "日 " + calendar.getLunar() + (BaseConfig.isDebug ? "测试专用" : ""));

        if (null == tInfo || TextUtils.isEmpty(tInfo.audio_path)) {
            //当天没有音频可以播放
            ToastUtil.showToast(getActivity(), "当天没有音频可以播放");
            return;
        }

        mTodayInfo = tInfo;
        updateBabyAge();
        likeBtn.setBackgroundResource(1 == mTodayInfo.is_collect ? R.drawable.icon_like_yes_white : R.drawable.icon_like_no);

        //进来默认选择第一个，所以自动播放
        ((MainActivity) getActivity()).showLoading();

        if (isFristSelect) {//进来默认选择第一个
            isFristSelect = false;
            int lastPlayDay = (int) PreferUtil.getInstance(getActivity()).getObject(PreferUtil.KEY_IS_FIRST_PLAY_TODY, -1);
            int today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
            if (today == lastPlayDay) {//今天播放过,不自动播放
                playerManager.setPlayerUrl(mTodayInfo.id, mTodayInfo.audio_path, false); //今天播放过不自动播放
                return;
            }
            PreferUtil.getInstance(getActivity()).putObject(PreferUtil.KEY_IS_FIRST_PLAY_TODY, today);
        }
        if (fragmentIndex != 0) {
            playerManager.setPlayerUrl(mTodayInfo.id, mTodayInfo.audio_path, false);
            return;
        }
        playerManager.setPlayerUrl(mTodayInfo.id, mTodayInfo.audio_path, true);
    }

    @Override
    public void onDestroyView() {
        if (null != playerManager) {
            playerManager.setDestory();
        }
        if (null != mBroadCastReceiver) {
            getActivity().unregisterReceiver(mBroadCastReceiver);
        }
        mHandler.removeCallbacks(runnable);
        super.onDestroyView();
        unbinder.unbind();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null == playerManager) {
                return;
            } else if (playerManager.isPlayOverMp3 || isPlayCompletion) {
                seekBar.setProgress(seekBar.getMax());
                return;
            }
            mHandler.postDelayed(this, 1000);
            int currentTime = Math
                    .round(playerManager.getCurrentPosition() / 1000);
            curSeekTv.setText(String.format("%02d:%02d", currentTime / 60, currentTime % 60));

            int totalTime = Math
                    .round(playerManager.getDuration() / 1000);

            totalSeekTv.setText(String.format("%02d:%02d", totalTime / 60, totalTime % 60));
            seekBar.setProgress(playerManager.getCurrentPosition());
        }
    };


    @OnClick({R.id.playBtn, R.id.likeBtn, R.id.shareBtn, R.id.historyBtnLy, R.id.docBtnLy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playBtn:
                if (null == mTodayInfo || TextUtils.isEmpty(mTodayInfo.audio_path)) {
                    ToastUtil.showToast(getActivity(), R.string.this_day_no_audio);
                    return;
                }
                if (playerManager.isPrepared) {
                    if (TextUtils.isEmpty(playerManager.curUrl)) {
                        playerManager.setPlayerUrl(mTodayInfo.id, mTodayInfo.audio_path, true);
                    } else {
                        playerManager.playOrPause();
                    }
                } else {
                    playerManager.setPlayerUrl(mTodayInfo.id, playerManager.curUrl, true);// 设置播放地址
                    mHandler.postDelayed(runnable, 1000);
                }

                break;
            case R.id.likeBtn:
                doLike();
                break;
            case R.id.shareBtn:
                ((MainActivity) getActivity()).mainActivityReqeusPermissions();
                break;
            case R.id.historyBtnLy:
                //历史提醒
                showHistory();
                break;
            case R.id.docBtnLy:
                //文稿
                if (null == mTodayInfo || TextUtils.isEmpty(mTodayInfo.description_href)) {
                    ToastUtil.showToast(getContext(), "当前没有文稿信息");
                    return;
                }

                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_KEY, new WebInfo("文稿", mTodayInfo.description_href));
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示历史
     */
    private void showHistory() {
        Dialog dialog = HistoryListPop.getInstance(getActivity()).showShareDialog();
        dialog.show();
    }

    //有读写权限才能成功
    public void hasPermission() {
        ShareDialog.getInstance(getActivity()).showShareDialog(new ShareBean("")).show();
    }


    public void fragmentChange(int index) {
        fragmentIndex = index;
        if (index != 0) {//用户切换到其他页面了
            if (null != playerManager && playerManager.isPlaying()) {
                playerManager.playOrPause();
            }
        } else {
            updateBabyAge();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPageEnd(getActivity(), "今日提醒");
//        if (null != playerManager && playerManager.isPlaying()) {
//            playerManager.playOrPause();
//        }
    }

    /**
     * 喜欢
     */
    private void doLike() {
        if (null == mTodayInfo) {
            ToastUtil.showToast(getActivity(), R.string.this_day_no_audio);
            return;
        }
        if (UserUtil.isLoginAndGoLoginActivity(getContext())) {
            mCollectRequest = CollectRequest.getInstance();
            mCollectRequest.collect((1 == mTodayInfo.is_collect), mTodayInfo.id, new CollectRequest.OnCollectRequestListener() {
                @Override
                public void onCollectRequestListener_result(boolean isSuccess, String msg) {
                    ToastUtil.showToast(getActivity(), msg);
                    if (isSuccess) {
                        mTodayInfo.is_collect = (mTodayInfo.is_collect == 0) ? 1 : 0;
                        likeBtn.setBackgroundResource(1 == mTodayInfo.is_collect ? R.drawable.icon_like_yes_white : R.drawable.icon_like_no);
                    }
                }
            });
        }
    }

    private void updatePlayIcon() {
        if (null != playerManager && playerManager.isPlaying() && !playerManager.isPlayOverMp3) {
            playBtn.setBackgroundResource(R.drawable.icon_playing);
        } else {
            playBtn.setBackgroundResource(R.drawable.icon_no_play);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (null != mTodayInfo && !TextUtils.isEmpty(mTodayInfo.audio_path) && null != playerManager && playerManager.isPlaying())
            playerManager.seekTo(seekBar.getProgress());
    }

    @Override
    public void playStateChange(boolean isPlayer) {
        ((MainActivity) getActivity()).cancelLoading();
        updatePlayIcon();
    }

    @Override
    public void onPrepared() { //播放器准备播放了
        ((MainActivity) getActivity()).cancelLoading();
        if (null != playerManager && !playerManager.isPlayOverMp3) {
            int currentTime = Math
                    .round(playerManager.getCurrentPosition() / 1000);
            curSeekTv.setText(String.format("%02d:%02d", currentTime / 60, currentTime % 60));

            int totaltime = Math
                    .round(playerManager.getDuration() / 1000);
            totalSeekTv.setText(String.format("%02d:%02d", totaltime / 60, totaltime % 60));
            seekBar.setProgress(0);
            seekBar.setMax(playerManager.getDuration());
            isPlayCompletion = false;
            mHandler.postDelayed(runnable, 0);
        }
    }

    @Override
    public void onCompletion() {
        mHandler.removeCallbacks(runnable);
        isPlayCompletion = true;
        ToastUtil.showToast(getContext(), "播放完成");

        int totaltime = Math
                .round(playerManager.getDuration() / 1000);
        totalSeekTv.setText(String.format("%02d:%02d", totaltime / 60, totaltime % 60));
        curSeekTv.setText(String.format("%02d:%02d", totaltime / 60, totaltime % 60));
        seekBar.setProgress(seekBar.getMax());
        playBtn.setBackgroundResource(R.drawable.icon_no_play);
        if (null != mTodayInfo) {
            mTodayInfo.is_study = 1;
        }
        initSelectMap();
    }

    @Override
    public void onRepeatUrl() {
        ((MainActivity) getActivity()).cancelLoading();
    }

    @Override
    public void onPlayError() {
        ToastUtil.showToast(getContext(), R.string.play_error);
        ((MainActivity) getActivity()).cancelLoading();
    }

    private boolean isPlayCompletion = false;
}
