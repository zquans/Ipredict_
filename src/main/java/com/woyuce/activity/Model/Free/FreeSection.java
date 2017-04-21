package com.woyuce.activity.Model.Free;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21
 */
public class FreeSection implements Serializable {

    public String sectionid;
    public String sectionname;
    public String sectioncolor;
    public String sectionstate;
    public String range_type;

    @Override
    public String toString() {
        return "Section [sectionid=" + sectionid + ", sectionname=" + sectionname + ", sectioncolor=" + sectioncolor
                + ", sectionstate=" + sectionstate + ", range_type=" + range_type + "]";
    }
}