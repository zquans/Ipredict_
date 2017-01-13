package com.woyuce.activity.Bean.Store;

/**
 * Created by Administrator on 2016/11/17.
 */
public class StoreAddress {

    //商城收货地址
    private String name;
    private String mobile;
    private String q_q;
    private String email;
    private String create_at;
    private String id;
    private String is_default;
    private String mobile_veri_code_id;
    private String verified_type;

    public StoreAddress() {
    }

    public String getVerified_type() {
        return verified_type;
    }

    public void setVerified_type(String verified_type) {
        this.verified_type = verified_type;
    }

    public String getMobile_veri_code_id() {
        return mobile_veri_code_id;
    }

    public void setMobile_veri_code_id(String mobile_veri_code_id) {
        this.mobile_veri_code_id = mobile_veri_code_id;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQ_q() {
        return q_q;
    }

    public void setQ_q(String q_q) {
        this.q_q = q_q;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    @Override
    public String toString() {
        return "StoreAddress{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", q_q='" + q_q + '\'' +
                ", email='" + email + '\'' +
                ", create_at='" + create_at + '\'' +
                ", id='" + id + '\'' +
                ", is_default='" + is_default + '\'' +
                ", mobile_veri_code_id='" + mobile_veri_code_id + '\'' +
                '}';
    }
}
