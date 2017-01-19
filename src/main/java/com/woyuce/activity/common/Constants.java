package com.woyuce.activity.common;

/**
 * Created by Administrator on 2016/10/7.
 */
public class Constants {

    public static final int CODE_CAMERE = 0x0001;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0x0002;
    public static final int CODE_READ_EXTERNAL_STORAGE = 0x0003;

    //四张重点图
    public static final String URL_GUIDE_IMG_NET = "http://www.iyuce.com/res/images/assault.jpg";
    public static final String URL_GUIDE_IMG_FREE = "http://www.iyuce.com/res/images/tl.jpg";
    public static final String URL_GUIDE_IMG_SPEAKING = "http://www.iyuce.com/res/images/ky.jpg";
    public static final String URL_GUIDE_IMG_WRITTING = "http://www.iyuce.com/res/images/xz.jpg";

    //API_TOKEN
    public static final String URL_API_REQUESTTOKEN = "http://api.iyuce.com/api/token";

    //WebView访问的URL汇总
    public static final String URL_WEB_ZHIBO = "https://iyuce.ke.qq.com/";
    public static final String URL_WEB_LUBO = "http://store.iyuce.com/";

    //网络班跳转商城获取商品信息
    public static final String URL_GetGoods = "http://api.iyuce.com/v1/store/getactivegoods";

    //免费范围
    public static final String ACTIVITY_RANGE = "range";
    public static final String ACTIVITY_BOOK = "book";
    public static final String ACTIVITY_LESSON = "lesson";
    public static final String ACTIVITY_PAGE = "page";
    public static final String ACTIVITY_CONTENT = "content";
    public static final String FRAGMENT_CONTENT = "answersheet";
    public static final String URL_POST_FREE_RANGE = "http://api.iyuce.com/v1/exam/free";
    public static final String URL_POST_FREE_LESSON = "http://api.iyuce.com/v1/exam/freeexamtype";
    public static final String URL_POST_FREE_BOOK = "http://api.iyuce.com/v1/exam/freeexamunits";
    public static final String URL_POST_FREE_SECTION = "http://api.iyuce.com/v1/exam/section";
    public static final String URL_POST_FREE_PAGE = "http://api.iyuce.com/v1/exam/subjects";
    public static final String URL_POST_NET_CHECKRIGHT = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    public static final String URL_POST_NET_CANCELTAG = "http://api.iyuce.com/v1/exam/cancelexams";
    public static final String URL_POST_FREE_TOANSWER = "http://api.iyuce.com/v1/exam/answers";
    public static final String URL_POST_FREE_ANSWER_TAGCAN = "http://api.iyuce.com/v1/exam/completepractice";
    public static final String URL_POST_FREE_ANSWER_TAGCANT = "http://api.iyuce.com/v1/exam/cancelexams";

    //网络班
    public static final String ACTIVITY_NET = "wangluoban";
    public static final String ACTIVITY_NET_LESSON = "wangluobanlesson";
    public static final String URL_POST_NET_TIME = "http://api.iyuce.com/v1/exam/notifydropdownlist";
    public static final String URL_POST_NET_NOTICE = "http://api.iyuce.com/v1/exam/notifycontent";
    public static final String URL_POST_NET_WEB_COURSE = "http://api.iyuce.com/v1/exam/webcoursegroup";
    public static final String URL_POST_NET_LESSON = "http://api.iyuce.com/v1/exam/examunit";
    public static final String URL_POST_NET_LESSON_Check = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    public static final String URL_POST_NET_LESSON_CheckCode = "http://api.iyuce.com/v1/exam/activecodeforwlb";

    //公益课堂音频
    public static final String URL_GET_AUDIO_TYPE = "http://api.iyuce.com/v1/exam/audiotypes";
    public static final String URL_POST_AUDIO_LIST = "http://api.iyuce.com/v1/exam/audios";

    //写作
    public static final String ACTIVITY_WIT = "writting";
    public static final String ACTIVITY_WIT_SEARCH = "witsearch";
    public static final String ACTIVITY_WIT_SUBCATEGORY = "witsubcategory";
    public static final String ACTIVITY_WIT_CONTENT = "witcontent";
    public static final String URL_POST_WRITTING_CATEGORY = "http://iphone.ipredicting.com/xzCategoryApi.aspx";
    public static final String URL_POST_WRITTING_SEARCH = "http://iphone.ipredicting.com/xzsubSearch.aspx";
    public static final String URL_POST_WRITTING_SUBCATEGORY = "http://iphone.ipredicting.com/xzsubCategory.aspx";
    public static final String URL_POST_WRITTING_TOTAL = "http://iphone.ipredicting.com/xzCategoryApi.aspx";
    public static final String URL_POST_WRITTING_CONTENT = "http://iphone.ipredicting.com/xzsubContent.aspx";

}