package cn.baby.love.common.bean;

import java.io.Serializable;
import java.util.List;

public class LanguageInfo implements Serializable {

    public List<LanguageInfo> data;

    public int id;
    public String name;
    public String add_time;

    @Override
    public String toString() {
        return name;
    }
}
