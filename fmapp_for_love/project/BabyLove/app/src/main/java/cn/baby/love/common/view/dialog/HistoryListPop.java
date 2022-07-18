package cn.baby.love.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.baby.love.R;
import cn.baby.love.adapter.LikeListAdapter;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.api.ApiCallback;
import cn.baby.love.common.api.CollectRequest;
import cn.baby.love.common.base.BaseActivity;
import cn.baby.love.common.base.BaseConfig;
import cn.baby.love.common.bean.LikeInfo;
import cn.baby.love.common.manager.MediaPlayerManager;
import cn.baby.love.common.manager.UserUtil;
import cn.baby.love.common.utils.ScreenUtil;
import cn.baby.love.common.utils.ToastUtil;
import cn.baby.love.common.view.JmRecyclerView;
import cn.baby.love.common.view.share.ShareBean;
import cn.baby.love.common.view.share.ShareDialog;

/**
 * 历史提醒
 */
public class HistoryListPop implements JmRecyclerView.OnRefreshListener,LikeListAdapter.OnItemClickListener, MediaPlayerManager.OnPlayerListener{
	private Activity mContext;
	private Dialog dialog;
	private JmRecyclerView mJmRecyclerView;
	private RecyclerView mRecyclerView;
	private LikeListAdapter adapter;
	private int curPage = 1;
	private LoadingDialog loadingDialog;
	private List<LikeInfo> infoList = new ArrayList<>();
	private boolean isLoaded = false;
	public Gson gson;
	private MediaPlayerManager playerManager;
	private CollectRequest mCollectRequest;

	private HistoryListPop(Activity activity) {
		this.mContext = activity;
		gson = new Gson();
		int screenWidth = ScreenUtil.getScreenWidth(mContext);
		dialog = CustomDialog.builder(mContext, R.layout.layout_history_list);
		RelativeLayout mLinearLayout = dialog.findViewById(R.id.whiteBgView);
		ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
		params.width = screenWidth;
		params.height = ScreenUtil.getScreenHeight(mContext) * 4 / 5;
		mLinearLayout.setLayoutParams(params);
		dialog.getWindow().setGravity(Gravity.BOTTOM);

		dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.cancel());
		mJmRecyclerView = dialog.findViewById(R.id.mJmRecyclerView);
		mJmRecyclerView.setOnRefreshListener(true, true, this);
		mRecyclerView = mJmRecyclerView.getmRecyclerView();
		mJmRecyclerView.showNormal();
		updateAdapter();
	}

	public static HistoryListPop getInstance(Activity activity) {
		return new HistoryListPop(activity);
	}


	public Dialog showShareDialog() {
		mJmRecyclerView.autoRefresh();
		return dialog;
	}


	@Override
	public void onRefresh() {
		curPage = 1;
		getHistoryList();
	}

	@Override
	public void onLoadMore() {
		curPage += 1;
		getHistoryList();
	}
	private void getHistoryList() {
		isLoaded = true;
		Api.getInstance().getHistoryList(curPage, collectApiCallback);

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
					ToastUtil.showToast(mContext, R.string.load_fail_try_again);
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
					ToastUtil.showToast(mContext, "没有更多数据了!");
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
				ToastUtil.showToast(mContext, R.string.net_error);
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
				ToastUtil.showToast(mContext, R.string.load_fail_try_again);
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

	private void updateAdapter() {
		if (null == adapter) {
			adapter = new LikeListAdapter(mContext, infoList);
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

	private void initPlayer() {
		if (null == playerManager) {
			playerManager = new MediaPlayerManager(mContext);
			playerManager.setOnPlayerListener(this);
			playerManager.initPlayer();
		}
	}

	public void cancelLoading() {
		if (null != loadingDialog && loadingDialog.isShowing()) {
			loadingDialog.cancel();
		}
	}
	public void showLoading() {
		if (null == loadingDialog) {
			loadingDialog = LoadingDialog.getInstance(mContext, mContext.getString(R.string.loading));
		}

		loadingDialog.setTipText(mContext.getString(R.string.loading));
		if (!loadingDialog.isShowing()) {
			loadingDialog.showGif();
		}
	}

	@Override
	public void onItemClickListener_clickShareIcon(LikeInfo info, int position) {
		((BaseActivity)mContext).requestWritePermissions();
		ShareDialog.getInstance(mContext).showShareDialog(new ShareBean("")).show();
	}

	@Override
	public void onItemClickListener_clicklikedIcon(LikeInfo info, int position) {
		if (null == info) {
			return;
		}
		if (UserUtil.isLoginAndGoLoginActivity(mContext)) {
			mCollectRequest = CollectRequest.getInstance();
			mCollectRequest.collect((1 == info.is_collect), info.id, new CollectRequest.OnCollectRequestListener() {
				@Override
				public void onCollectRequestListener_result(boolean isSuccess, String msg) {
					ToastUtil.showToast(mContext, msg);
					if (isSuccess) {
						infoList.get(position).is_collect = infoList.get(position).is_collect == 0 ? 1 : 0;
					}
					updateAdapter();
				}
			});
		}
	}

	@Override
	public void onItemClickListener_clickPlayIcon(LikeInfo info, int position) {
		adapter.curLikeInfo = info;
		if (null == adapter.curLikeInfo || TextUtils.isEmpty(adapter.curLikeInfo.audio_path)) {
			ToastUtil.showToast(mContext, "无效播放地址");
			return;
		}

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
		mContext.sendBroadcast(new Intent(BaseConfig.ACTION_NOTIFY_OTHER_PLAYER_PAUSE));
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
		ToastUtil.showToast(mContext, "播放完成");
		for (LikeInfo info : infoList) {
			info.isPlaying = false;
		}
		updateAdapter();
	}

	@Override
	public void onRepeatUrl() {
		cancelLoading();
	}

	@Override
	public void onPlayError() {
		ToastUtil.showToast(mContext, R.string.play_error);
		cancelLoading();
	}
}
