package com.woyuce.activity.Model.Free;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21
 */
public class FreePage implements Serializable {

    public String sub_id;
    public String sub_name;
    public String sub_color;
    public String sub_img;
    public String sub_state;
    public String sub_range_type;
    public String addtime;
    public String sub_img_empty;

    @Override
    public String toString() {
        return "Page [sub_id=" + sub_id + ", sub_name=" + sub_name + ", sub_color=" + sub_color + ", sub_img=" + sub_img
                + ", sub_state=" + sub_state + ", sub_range_type=" + sub_range_type + ", addtime=" + addtime
                + ", sub_img_empty=" + sub_img_empty + "]";
    }

}