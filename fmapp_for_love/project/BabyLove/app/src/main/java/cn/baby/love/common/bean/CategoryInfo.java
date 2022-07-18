package cn.baby.love.common.bean;

import java.util.List;

public class CategoryInfo {
    public List<CategoryInfo> data;
    public int id;
    public String name;
    public String img;
    public int pid;
    public int audio_numebr;

    public List<CategoryInfoChild> child;
}
