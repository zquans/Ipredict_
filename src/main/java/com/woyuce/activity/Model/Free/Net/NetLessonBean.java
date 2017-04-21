package com.woyuce.activity.Model.Free.Net;

/**
 * Created by Administrator on 2016/9/21
 */
public class NetLessonBean {

    private String unitName;
    private String unitId;
    private String showTypeId;
    private String imgPath;

    public NetLessonBean() {
        super();
    }

    public NetLessonBean(String unitName, String unitId, String showTypeId, String imgPath) {
        super();
        this.unitName = unitName;
        this.unitId = unitId;
        this.showTypeId = showTypeId;
        this.imgPath = imgPath;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getShowTypeId() {
        return showTypeId;
    }

    public void setShowTypeId(String showTypeId) {
        this.showTypeId = showTypeId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "WangLesson [unitName=" + unitName + ", unitId=" + unitId + ", showTypeId=" + showTypeId + ", imgPath="
                + imgPath + "]";
    }
}