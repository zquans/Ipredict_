package com.woyuce.activity.Model.Store;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/25
 */
public class StoreOrder {

    private String id;
    private String order_no;
    private String price;
    private String discount_price;
    private String actual_price;
    private String create_at;
    private String order_status;
    private String contact_person;
    private String contact_tel;
    private String contact_q_q;
    private String contact_email;
    private String is_cancel;
    private String cancel_at;
    private String is_del;
    private String cancel_by;
    private ArrayList<StoreGoods> user_order_details = new ArrayList<>();

    public StoreOrder() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getActual_price() {
        return actual_price;
    }

    public void setActual_price(String actual_price) {
        this.actual_price = actual_price;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getContact_tel() {
        return contact_tel;
    }

    public void setContact_tel(String contact_tel) {
        this.contact_tel = contact_tel;
    }

    public String getContact_q_q() {
        return contact_q_q;
    }

    public void setContact_q_q(String contact_q_q) {
        this.contact_q_q = contact_q_q;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getIs_cancel() {
        return is_cancel;
    }

    public void setIs_cancel(String is_cancel) {
        this.is_cancel = is_cancel;
    }

    public String getCancel_at() {
        return cancel_at;
    }

    public void setCancel_at(String cancel_at) {
        this.cancel_at = cancel_at;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getCancel_by() {
        return cancel_by;
    }

    public void setCancel_by(String cancel_by) {
        this.cancel_by = cancel_by;
    }

    public ArrayList<StoreGoods> getUser_order_details() {
        return user_order_details;
    }

    public void setUser_order_details(ArrayList<StoreGoods> user_order_details) {
        this.user_order_details = user_order_details;
    }

    @Override
    public String toString() {
        return "StoreOrder{" +
                "id='" + id + '\'' +
                ", order_no='" + order_no + '\'' +
                ", price='" + price + '\'' +
                ", discount_price='" + discount_price + '\'' +
                ", actual_price='" + actual_price + '\'' +
                ", create_at='" + create_at + '\'' +
                ", order_status='" + order_status + '\'' +
                ", contact_person='" + contact_person + '\'' +
                ", contact_tel='" + contact_tel + '\'' +
                ", contact_q_q='" + contact_q_q + '\'' +
                ", contact_email='" + contact_email + '\'' +
                ", is_cancel='" + is_cancel + '\'' +
                ", cancel_at='" + cancel_at + '\'' +
                ", is_del='" + is_del + '\'' +
                ", cancel_by='" + cancel_by + '\'' +
                ", user_order_details=" + user_order_details +
                '}';
    }
}