package com.woyuce.activity.Model.Free;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FreeSection {

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