package cn.baby.love.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.baby.love.R;
import cn.baby.love.adapter.LikeListAdapter;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.CollectRequest;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.bean.CategoryInfoChild;
import cn.baby.love.common.bean.LikeInfo;
import cn.baby.love.common.manager.MediaPlayerManager;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.JmRecyclerView;
import cn.baby.love.common.view.dialog.CustomDialog;
import cn.baby.love.common.view.share.ShareBean;
import cn.baby.love.common.view.share.ShareDialog;

/**
 * 我喜欢的
 */
public class LikedActivity extends BaseActivity implements JmRecyclerView.OnRefreshListener,
        LikeListAdapter.OnItemClickListener, MediaPlayerManager.OnPlayerListener {

    private JmRecyclerView mJmRecyclerView;
    private RecyclerView mRecyclerView;
    private List<LikeInfo> infoList = new ArrayList<>();
    private LikeListAdapter adapter;
    private int curPage = 1;

    /**
     * 0我的收藏，1搜索列表，2专家严选
     */
    public static final String KEY_PARAM_TYPE = "key_param_type";
    public static final String KEY_PARAM_INFO = "key_param_INFO";
    public static final String KEY_PARAM_TITLE = "key_param_title";
    public static final String KEY_PARAM_SEARCH_CONTENT = "key_param_search_conent";

    private String title;
    private int type;
    private CategoryInfoChild mExportItemInfo;
    private CollectRequest mCollectRequest;
    private String searchContent;
    private MediaPlayerManager playerManager;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra(KEY_PARAM_TYPE, 0);
        title = intent.getStringExtra(KEY_PARAM_TITLE);
        mExportItemInfo = (CategoryInfoChild) intent.getSerializableExtra(KEY_PARAM_INFO);
        searchContent = intent.getStringExtra(KEY_PARAM_SEARCH_CONTENT);
        initTitle(title);
//        rightIcon.setVisibility(View.VISIBLE);
        rightIcon.setImageResource(R.drawable.icon_sort);
        rightTv1.setTextColor(getResources().getColor(R.color.white));
        rightTv2.setTextColor(getResources().getColor(R.color.white));
//        rightTv1.setVisibility(View.VISIBLE);
//        rightTv2.setVisibility(View.VISIBLE);
        rightTv1.setText("/0");
        rightTv2.setText("0");
        TextPaint tp = rightTv2.getPaint();
        tp.setFakeBoldText(true);

        mJmRecyclerView = (JmRecyclerView) findViewById(R.id.mJmRecyclerView);
        mJmRecyclerView.setOnRefreshListener(true, true, this);
        mRecyclerView = mJmRecyclerView.getmRecyclerView();
        mJmRecyclerView.showNormal();
        updateAdapter();
        showLoading();
        getLikeInfos();
    }

    private void updateAdapter() {
        rightTv1.setText("/" + infoList.size());
        if (null == adapter) {
            adapter = new LikeListAdapter(this, infoList);
            adapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if (isLoaded && infoList.size() <= 0) {
            mJmRecyclerView.showEmpty();
            return;
        }

    }

    private void getLikeInfos() {
        isLoaded = true;
        if (1 == type) {//搜索
            Api.getInstance().search(curPage, searchContent, searchApiCallback);
        } else if (0 == type) {//我的收藏
            Api.getInstance().getCollectList(curPage, collectApiCallback);
        } else {//专家严选
            Api.getInstance().getExportInfoList(curPage, mExportItemInfo.id, mExportItemInfo.pid, collectApiCallback);
        }
    }

    ApiCallback collectApiCallback = new ApiCallback() {
        @Override
        public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
            cancelLoading();
            if (!isSuccess) {
                if (curPage <= 1) {
                    mJmRecyclerView.showFail();
                } else {
                    curPage--;
                    ToastUtil.showToast(LikedActivity.this, R.string.load_fail_try_again);
                    mJmRecyclerView.showNormal();
                }
            } else {//成功
                mJmRecyclerView.showNormal();
                JSONObject dataObj = result.optJSONObject("data");
                LikeInfo infos = gson.fromJson(dataObj.toString(), LikeInfo.class);
                if (null != infos && null != infos.data && infos.data.size() > 0) {
                    if (curPage == 1) {
                        infoList.clear();
                    }
                    infoList.addAll(infos.data);
                } else {
                    ToastUtil.showToast(LikedActivity.this, "没有更多数据了!");
                }
                dealInfoList();
                initPlayer();
                updateAdapter();
            }
        }

        @Override
        public void onNotNetwork(String msg) {
            super.onNotNetwork(msg);
            cancelLoading();
            if (curPage <= 1) {
                mJmRecyclerView.showNetErr();
            } else {
                curPage--;
                ToastUtil.showToast(LikedActivity.this, R.string.net_error);
                mJmRecyclerView.showNormal();
            }
        }

        @Override
        public void onFail(Response<String> response, String msg) {
            super.onFail(response, msg);
            cancelLoading();
            if (curPage <= 1) {
                mJmRecyclerView.showFail();
            } else {
                curPage--;
                ToastUtil.showToast(LikedActivity.this, R.string.load_fail_try_again);
                mJmRecyclerView.showNormal();
            }
        }
    };

    private void dealInfoList() {
        if (null != adapter.curLikeInfo) {
            for (LikeInfo mLikeInfo : infoList) {
                if (adapter.curLikeInfo.id == mLikeInfo.id) {
                    mLikeInfo.isPlaying = adapter.curLikeInfo.isPlaying;
                } else {
                    mLikeInfo.isPlaying = false;
                }
            }
        }
    }

    ApiCallback searchApiCallback = new ApiCallback() {
        @Override
        public void onSuccess(Response<String> response, boolean isSuccess, JSONObject result) {
            cancelLoading();
            if (!isSuccess) {
                if (curPage <= 1) {
                    mJmRecyclerView.showFail();
                } else {
                    curPage--;
                    mJmRecyclerView.showNormal();
                    ToastUtil.showToast(LikedActivity.this, R.string.load_fail_try_again);
                }
            } else {//成功
                mJmRecyclerView.showNormal();
                JSONObject dataObj = result.optJSONObject("data");

                LikeInfo infos = gson.fromJson(dataObj.toString(), LikeInfo.class);
                if (null != infos && null != infos.data && infos.data.size() > 0) {
                    if (curPage == 1) {
                        infoList.clear();
                    }
                    infoList.addAll(infos.data);
                    dealInfoList();
                }
                initPlayer();
                updateAdapter();
            }
        }

        @Override
        public void onNotNetwork(String msg) {
            super.onNotNetwork(msg);
            cancelLoading();
            if (curPage <= 1) {
                mJmRecyclerView.showNetErr();
            } else {
                mJmRecyclerView.showNormal();
                curPage--;
                ToastUtil.showToast(LikedActivity.this, R.string.net_error);
            }
        }

        @Override
        public void onFail(Response<String> response, String msg) {
            super.onFail(response, msg);
            cancelLoading();
            if (curPage <= 1) {
                mJmRecyclerView.showFail();
            } else {
                mJmRecyclerView.showNormal();
                curPage--;
                ToastUtil.showToast(LikedActivity.this, R.string.load_fail_try_again);
            }
        }
    };

    @Override
    public void onRefresh() {
        curPage = 1;
        getLikeInfos();
    }

    @Override
    public void onLoadMore() {
        curPage += 1;
        getLikeInfos();
    }

    @Override
    public void onItemClickListener_clickShareIcon(LikeInfo info, int position) { //分享
        checkPermissions();
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        requestWritePermissions();
    }


    @Override
    public void hasPermissions() {
        super.hasPermissions();

        ShareDialog.getInstance(this).showShareDialog(new ShareBean("")).show();//

    }

    @Override
    public void onItemClickListener_clicklikedIcon(LikeInfo info, final int position) { //收藏
        if (null == info) {
            return;
        }
        if (UserUtil.isLoginAndGoLoginActivity(LikedActivity.this)) {
            mCollectRequest = CollectRequest.getInstance();
            mCollectRequest.collect((1 == info.is_collect), info.id, new CollectRequest.OnCollectRequestListener() {
                @Override
                public void onCollectRequestListener_result(boolean isSuccess, String msg) {
                    ToastUtil.showToast(LikedActivity.this, msg);
                    if (isSuccess) {
                        infoList.get(position).is_collect = infoList.get(position).is_collect == 0 ? 1 : 0;
                    }
                    updateAdapter();
                }
            });
        }
    }

    @OnClick({R.id.rightLayout, R.id.leftIconLy})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.leftIconLy) {
            finish();
        } else if (id == R.id.rightLayout) {
            if (null != infoList) {
                List<LikeInfo> infos = new ArrayList<>();
                for (int i = infoList.size() - 1; i >= 0; i--) {
                    LikeInfo info = infoList.get(i);
                    infos.add(info);
                }
                infoList.clear();
                infoList.addAll(infos);
                updateAdapter();

                if (null == adapter.curLikeInfo) {
                    return;
                }
                for (int i = 0; i < infoList.size(); i++) {
                    LikeInfo likeInfo = infoList.get(i);
                    if (likeInfo.id == adapter.curLikeInfo.id) {
                        rightTv2.setText("" + (i + 1));
                    }
                }

            }
        }
    }

    @Override
    public void onItemClickListener_clickPlayIcon(LikeInfo info, int position) { //播放按钮
        adapter.curLikeInfo = info;
        if (null == adapter.curLikeInfo || TextUtils.isEmpty(adapter.curLikeInfo.audio_path)) {
            ToastUtil.showToast(this, "无效播放地址");
            return;
        }

        rightTv2.setText("" + (position + 1));

        if (TextUtils.isEmpty(playerManager.curUrl) || !playerManager.curUrl.equals(adapter.curLikeInfo.audio_path)) {
            for (LikeInfo minfo : infoList) {
                minfo.isPlaying = false;
            }
            showLoading();
            playerManager.setPlayerUrl(adapter.curLikeInfo.id, adapter.curLikeInfo.audio_path, true);
        } else {
            playerManager.playOrPause();
        }

        adapter.curLikeInfo.isPlaying = playerManager.isPlaying() ? true : false;

        notifyOtherPlayerPause();

        updateAdapter();
    }

    /**
     * 通知其他播放器暂停播放
     */
    private void notifyOtherPlayerPause() {
        sendBroadcast(new Intent(BaseConfig.ACTION_NOTIFY_OTHER_PLAYER_PAUSE));
    }

    private void initPlayer() {
        if (null == playerManager) {
            playerManager = new MediaPlayerManager(this);
            playerManager.setOnPlayerListener(this);
            playerManager.initPlayer();
        }
    }

    @Override
    public void playStateChange(boolean isPlayer) {
        cancelLoading();
    }

    @Override
    public void onPrepared() {
        cancelLoading();
        adapter.curLikeInfo.isPlaying = playerManager.isPlaying() ? true : false;

        if (null != adapter.curLikeInfo && playerManager.isPlaying()) {
            adapter.curLikeInfo.isPlaying = true;
        } else {
            adapter.curLikeInfo.isPlaying = false;
        }


        updateAdapter();
    }

    @Override
    public void onCompletion() {
        ToastUtil.showToast(this, "播放完成");
        for (LikeInfo info : infoList) {
            info.isPlaying = false;
        }
        updateAdapter();

    }

    @Override
    public void onPlayError() {
        ToastUtil.showToast(this, R.string.play_error);
        cancelLoading();
    }

    @Override
    public void onRepeatUrl() {
        cancelLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != playerManager) {
            playerManager.setDestory();
        }
    }
}
