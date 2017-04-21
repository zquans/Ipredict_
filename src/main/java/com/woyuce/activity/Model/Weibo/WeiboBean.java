package com.woyuce.activity.Model.Weibo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/23
 */
public class WeiboBean {

//    public String user_name;
//    public String pulish_time;
//    public String pulish_message;
//    public String comment;
//    public String like;
//    public String img_url; //发的图片
//    public String has_photo;

    public String body;   //内容
    public String pulish_image; //发的图片
    public String author; //作者
    public String date_created;//时间
    public String avatar_url;//头像？
    public String reply_count;//回复数
    public String subject;
    public String commented_object_id;
    public int microblog_id;//微博ID
    public int parent_id;

    public ArrayList<String> mImgList = new ArrayList<>();//图片数组

    public WeiboBean() {
    }

    @Override
    public String toString() {
        return "WeiboBean{" +
                "body='" + body + '\'' +
                ", pulish_image='" + pulish_image + '\'' +
                ", author='" + author + '\'' +
                ", date_created='" + date_created + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", reply_count='" + reply_count + '\'' +
                ", subject='" + subject + '\'' +
                ", commented_object_id='" + commented_object_id + '\'' +
                ", microblog_id=" + microblog_id +
                ", parent_id=" + parent_id +
                ", mImgList=" + mImgList +
                '}';
    }

//        "user_name": "用户名大伟",
//        "pulish_time": "发表时间2014-02-11 11:25:30",
//        "pulish_message": "发表内容",
//        "pulish_image": "图片(可能多图，写成数组)",
//        “comment”: “评论（可能多评论，写成数组）”,
//        “like”: “263赞 ”,


}
