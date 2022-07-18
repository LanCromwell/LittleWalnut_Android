package cn.baby.love.common.bean;

import java.util.List;

public class LikeInfo {
    public LikeInfo(String title){
        this.title = title;
    }

    public List<LikeInfo> data;

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
    public int is_collect; //0未收藏，1收藏
    public int is_study;//是否学习过，0未学习，1已学习
    public boolean isPlaying;

    /**
     * 标签
     */
    public List<String> tag;

    @Override
    public String toString() {
        return "LikeInfo{" +
                "data=" + data +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", duration='" + duration + '\'' +
                ", play_number=" + play_number +
                ", collect_number=" + collect_number +
                ", language_id=" + language_id +
                ", category_id=" + category_id +
                ", add_time='" + add_time + '\'' +
                ", audio_path='" + audio_path + '\'' +
                ", type=" + type +
                ", is_collect=" + is_collect +
                ", is_study=" + is_study +
                ", isPlaying=" + isPlaying +
                ", tag=" + tag +
                '}';
    }
}
