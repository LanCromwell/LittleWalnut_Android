package cn.baby.love.common.manager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

import cn.baby.love.App;
import cn.baby.love.common.api.Api;
import cn.baby.love.common.bean.UserBean;
import cn.baby.love.common.utils.LogUtil;
import cn.baby.love.common.utils.ToastUtil;

public class MediaPlayerManager {
    private Context context;
    private MediaPlayer player;
    public boolean isPrepared = false;
    /**
     * 页面是否已经销毁
     */
    private boolean isDestory = false;
    public String curUrl;//当前播放的地址
    //是否在播结束的语音
    public boolean isPlayOverMp3 = false;

    public MediaPlayerManager(Context context) {
        this.context = context;
    }

    public void initPlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.i("播放完成");
                //播放结束
                if (isPlayOverMp3) {//结尾mp3播放完成
                    isPlayOverMp3 = false;
                } else {//正常语音播放完成，播放结束语音
                    isPlayOverMp3 = true;
                    if (null != mOnPlayerListener) {
                        mOnPlayerListener.onCompletion();
                    }
                    changeVoice(true, true);
                }
                curUrl = null;
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //播放错误
                if (null != mOnPlayerListener) {
                    mOnPlayerListener.onPlayError();
                }
                return true;
            }
        });
    }

    public MediaPlayer getPlayer() {
        if (null == player) {
            initPlayer();
        }
        return player;
    }

    public void setDestory() {
        isDestory = true;
        releasePlayer();
    }

    /**
     * 设置播放地址并开始播放
     *
     * @param url
     */
    public void setPlayerUrl(int audio_id, String url, boolean isAutoPlay) {
        isPlayOverMp3 = false;
        try {
            if (null != player && !TextUtils.isEmpty(url)) {
                if (url.equals(curUrl)) {
                    if (null != mOnPlayerListener) {
                        mOnPlayerListener.onRepeatUrl();
                    }
                    return;
                }
                curUrl = url;
                Api.getInstance().addPlayCount(audio_id);//通知服务器增加播放量
                changeVoice(false, isAutoPlay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(context, "播放失败，请播放其他音频");
        }
    }

    /**
     * 播放结束语音
     */
    private AssetFileDescriptor getOverMp3() {
        AssetFileDescriptor fd = null;
        try {
            if (UserUtil.isLogin()) {
                UserBean userBean = UserUtil.getUserInfo();
                if (3 == userBean.role_id) {//祖父母
                    fd = App.mInstance.getAssets().openFd("gradfather_good.mp3");
                } else if (2 == userBean.role_id) {//爸爸
                    fd = App.mInstance.getAssets().openFd("father_good.mp3");
                } else {//妈妈
                    fd = App.mInstance.getAssets().openFd("mather_good.mp3");
                }
            } else {
                fd = App.mInstance.getAssets().openFd("mather_good.mp3");
            }

            return fd;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 切换音乐
     *
     * @param isPlayOverVoice
     * @param isAutoPlay      是否自动播放
     */
    private void changeVoice(boolean isPlayOverVoice, final boolean isAutoPlay) {
        try {
            player.pause();
            player.stop();
            player.reset();
            if (isPlayOverVoice) {
                AssetFileDescriptor fd = getOverMp3();
                if (null == fd) {
                    return;
                }
                player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            } else {
                player.setDataSource(curUrl);
            }
            player.prepareAsync();
            isPrepared = false;
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.i("准备完成");
                    if (isDestory) {
                        return;
                    }
                    isPrepared = true;
                    //播放器准备完毕开始播放
                    if (isAutoPlay) {
                        player.start();
                    }
                    if (null != mOnPlayerListener && !isPlayOverMp3) {
                        mOnPlayerListener.playStateChange(player.isPlaying());
                        mOnPlayerListener.onPrepared();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (null != player && player.isPlaying()) {
            player.pause();
            if (null != mOnPlayerListener) {
                mOnPlayerListener.playStateChange(player.isPlaying());
            }
        }
    }

    /**
     * 暂停或者播放
     */
    public void playOrPause() {
        if (null != player) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
            if (null != mOnPlayerListener) {
                mOnPlayerListener.playStateChange(player.isPlaying());
            }
        }
    }

    public int getCurrentPosition() {
        if (null != player) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (null != player && null != curUrl && isPrepared) {
            return player.getDuration();
        }
        return 0;
    }

    public void releasePlayer() {
        if (null != player) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public void seekTo(int mills) {
        if (null != player) {
            player.seekTo(mills);
        }
    }

    public boolean isPlaying() {
        if (null != player) {
            return player.isPlaying();
        }
        return false;
    }

    private OnPlayerListener mOnPlayerListener;

    public void setOnPlayerListener(OnPlayerListener mOnPlayerListener) {
        this.mOnPlayerListener = mOnPlayerListener;
    }

    public interface OnPlayerListener {
        void playStateChange(boolean isPlayer);

        void onPrepared();

        void onCompletion();

        void onRepeatUrl();

        void onPlayError();
    }

}
