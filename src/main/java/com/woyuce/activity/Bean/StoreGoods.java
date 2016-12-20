package com.woyuce.activity.Bean;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreGoods {

    private String sales_price;
    private String goods_title;
    private String thumb_img;
    private String goods_id;
    private String goods_sku_id;

    //商品规格相关
    private String attr_id;
    private String attr_text;
    private String attr_clickable;

    //商品评论相关
    private String comment_text;//评论内容
    private String satisfaction;//好评中评差评
    private String create_at;//评论时间
    private String create_by_name;//评论用户

    private String img_url; //晒单图片
    private String show_at; //晒单时间

    //订单Order相关
    private String id;
    private String order_id;
    private String goods_thumb_img_url;
    private String is_show_order;
    private String is_comment;
    private String unit_price;
    private String total_price;
    private String quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StoreGoods() {
    }

    public String getGoods_thumb_img_url() {
        return goods_thumb_img_url;
    }

    public void setGoods_thumb_img_url(String goods_thumb_img_url) {
        this.goods_thumb_img_url = goods_thumb_img_url;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGoods_property() {
        return goods_property;
    }

    public void setGoods_property(String goods_property) {
        this.goods_property = goods_property;
    }

    private String goods_property;


    public String getAttr_clickable() {
        return attr_clickable;
    }

    public void setAttr_clickable(String attr_clickable) {
        this.attr_clickable = attr_clickable;
    }

    public String getAttr_id() {
        return attr_id;
    }

    public void setAttr_id(String attr_id) {
        this.attr_id = attr_id;
    }

    public String getAttr_text() {
        return attr_text;
    }

    public void setAttr_text(String attr_text) {
        this.attr_text = attr_text;
    }

    public String getGoods_title() {
        return goods_title;
    }

    public void setGoods_title(String goods_title) {
        this.goods_title = goods_title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getShow_at() {
        return show_at;
    }

    public void setShow_at(String show_at) {
        this.show_at = show_at;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getCreate_by_name() {
        return create_by_name;
    }

    public void setCreate_by_name(String create_by_name) {
        this.create_by_name = create_by_name;
    }

    public String getSales_price() {
        return sales_price;
    }

    public void setSales_price(String sales_price) {
        this.sales_price = sales_price;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thumb_img) {
        this.thumb_img = thumb_img;
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
}