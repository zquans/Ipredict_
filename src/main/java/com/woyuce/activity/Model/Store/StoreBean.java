package com.woyuce.activity.Model.Store;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreBean {

    //商城轮播图相关
    private String menudata;
    private String icon_mobile_url;
    private String goods_id;
    private String goods_sku_id;
    private String goods_title;
    private String sales_price;

    //商城首页展示实体
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

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_sku_id() {
        return goods_sku_id;
    }

    public void setGoods_sku_id(String goods_sku_id) {
        this.goods_sku_id = goods_sku_id;
    }

    public String getGoods_title() {
        return goods_title;
    }

    public void setGoods_title(String goods_title) {
        this.goods_title = goods_title;
    }

    public String getSales_price() {
        return sales_price;
    }

    public void setSales_price(String sales_price) {
        this.sales_price = sales_price;
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
