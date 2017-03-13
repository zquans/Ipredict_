package com.woyuce.activity.common;

/**
 * Created by Administrator on 2016/10/7
 */
public class Constants {

    public static final int CODE_CAMERE = 0x0001;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0x0002;
    public static final int CODE_READ_EXTERNAL_STORAGE = 0x0003;

    //数据库相关
    public static final String DATABASE_AIPU = "aipu.db";
    public static final String TABLE_STORE = "storetb";
    public static final String COLUMN_SKU_ID = "goodsskuid";
    public static final String DATABASE_IYUCE = "IYUCE.db";
    public static final String TABLE_SQLITE_MASTER = "sqlite_master";   //sql系统表
    public static final String TABLE_NAME = "tbl_name";  //sql系统表中的字段
    public static final String NAME = "name";            //sql系统表中的字段
    public static final String TABLE_CART = "cart_table";
    public static final String COLUMN_GOODS_ID = "Goods_id";
    public static final String COLUMN_GOODS_SPEC_ID = "Goods_spec_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SPEC_NAME = "Spec_name";
    public static final String COLUMN_COUNT = "Count";
    public static final String COLUMN_PRICE = "Price";
    public static final String NONE = "none";

    //URL
    public static final String URL_API_REQUESTTOKEN = "http://api.iyuce.com/api/token";

    //WebView访问的URL汇总
    public static final String URL_WEB_ZHIBO = "https://iyuce.ke.qq.com/";
    public static final String URL_WEB_LUBO = "http://store.iyuce.com/";
    //网络班跳转商城获取商品信息
    public static final String URL_GetGoods = "http://api.iyuce.com/v1/store/getactivegoods";

//    public static final String URL_WEB_LUBO = "https://iyuce.ke.qq.com/";

    //公益课堂音频接口
    public static final String URL_GET_AUDIO_TYPE = "http://api.iyuce.com/v1/exam/audiotypes";
    public static final String URL_POST_AUDIO_LIST = "http://api.iyuce.com/v1/exam/audios";
}
