package com.woyuce.activity.Model.Free;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21
 */
public class FreeLesson implements Serializable {

    public String title;
    public String image;
    public String url;
    public String type_id;
    public String user_power_type_id;

    @Override
    public String toString() {
        return "Lesson [title=" + title + ", image=" + image + ", url=" + url + ", type_id=" + type_id
                + ", user_power_type_id=" + user_power_type_id + "]";
    }
}