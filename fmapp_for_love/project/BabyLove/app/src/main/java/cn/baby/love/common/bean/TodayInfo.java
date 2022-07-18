package cn.baby.love.common.bean;

import java.util.List;

public class TodayInfo {
    public List<TodayInfo> data;
    public int id;
    public String title;
    public String duration;
    public int play_number;
    public int collect_number;
    public int language_id;
    public int category_id;
    public String add_time;
    public String audio_path;
    public int type;
    public long remind_date;
    public int is_collect;//0未收藏，1收藏
    public int is_study;//0未学习，1已学习
    public int day;

    public String description_href;//文稿链接



}
