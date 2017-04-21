package com.woyuce.activity.Model.Free.Net;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21
 */
public class NetBean implements Serializable {

    private String wcg_id;
    private String wcg_powerid;
    private String wcg_name;
    private String MonthId;
    private String MobileDisplayType;
    private String imgUrl;

    public NetBean() {
        super();
    }

    public NetBean(String wcg_id, String wcg_powerid, String wcg_name, String monthId, String mobileDisplayType,
                   String imgUrl) {
        super();
        this.wcg_id = wcg_id;
        this.wcg_powerid = wcg_powerid;
        this.wcg_name = wcg_name;
        MonthId = monthId;
        MobileDisplayType = mobileDisplayType;
        this.imgUrl = imgUrl;
    }

    public String getWcg_id() {
        return wcg_id;
    }

    public void setWcg_id(String wcg_id) {
        this.wcg_id = wcg_id;
    }

    public String getWcg_powerid() {
        return wcg_powerid;
    }

    public void setWcg_powerid(String wcg_powerid) {
        this.wcg_powerid = wcg_powerid;
    }

    public String getWcg_name() {
        return wcg_name;
    }

    public void setWcg_name(String wcg_name) {
        this.wcg_name = wcg_name;
    }

    public String getMonthId() {
        return MonthId;
    }

    public void setMonthId(String monthId) {
        MonthId = monthId;
    }

    public String getMobileDisplayType() {
        return MobileDisplayType;
    }

    public void setMobileDisplayType(String mobileDisplayType) {
        MobileDisplayType = mobileDisplayType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "WcgBean [wcg_id=" + wcg_id + ", wcg_powerid=" + wcg_powerid + ", wcg_name=" + wcg_name + ", MonthId="
                + MonthId + ", MobileDisplayType=" + MobileDisplayType + ", imgUrl=" + imgUrl + "]";
    }

}
