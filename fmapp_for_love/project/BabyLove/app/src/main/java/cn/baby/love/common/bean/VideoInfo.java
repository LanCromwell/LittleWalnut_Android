package cn.baby.love.common.bean;

import java.util.List;

public class VideoInfo {
    public VideoInfo(String title){
        this.title = title;
    }

    public List<VideoInfo> data;

    public int id;
    public String title;
    public int duration;
    public int play_number;
    public int collect_number;
    public int language_id;
    public int category_id;
    public String add_time;
    public String audio_path;
    public int type;
    public int is_collect;

    public boolean isPlaying;
}
