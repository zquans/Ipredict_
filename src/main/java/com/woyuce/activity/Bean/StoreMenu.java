package com.woyuce.activity.Bean;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreMenu {

    private String _id;
    private String name;
    private String id;
    private String num;
    private String price;
    private String goodsskuid;

    public StoreMenu() {
    }

    public String getGoodsskuid() {
        return goodsskuid;
    }

    public void setGoodsskuid(String goodsskuid) {
        this.goodsskuid = goodsskuid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StoreMenu{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", num='" + num + '\'' +
                ", price='" + price + '\'' +
                ", goodsskuid='" + goodsskuid + '\'' +
                '}';
    }
}