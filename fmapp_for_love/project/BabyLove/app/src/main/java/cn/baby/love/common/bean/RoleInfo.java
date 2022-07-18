package cn.baby.love.common.bean;

import java.io.Serializable;
import java.util.List;

public class RoleInfo implements Serializable {

    public List<RoleInfo> data;

    public int id;
    public String name;
    public String add_time;

    @Override
    public String toString() {
        return name;
    }
}
