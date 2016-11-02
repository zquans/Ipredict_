package com.woyuce.activity.Bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreBean {

    private String menudata;
    private String icon_mobile_url;

    private String title;

    private String menu;
    private ArrayList<StoreGoods> goods_result = new ArrayList<>();

    public StoreBean() {
    }

    public ArrayList<StoreGoods> getGoods_result() {
        return goods_result;
    }

    public void setGoods_result(ArrayList<StoreGoods> goods_result) {
        this.goods_result = goods_result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenudata() {
        return menudata;
    }

    public void setMenudata(String menudata) {
        this.menudata = menudata;
    }

    public String getIcon_mobile_url() {
        return icon_mobile_url;
    }

    public void setIcon_mobile_url(String icon_mobile_url) {
        this.icon_mobile_url = icon_mobile_url;
    }
}
