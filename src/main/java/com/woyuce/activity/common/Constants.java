package com.woyuce.activity.common;

/**
 * Created by Administrator on 2016/10/7.
 */
public class Constants {

    public static final int CODE_CAMERE = 0x0001;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0x0002;
    public static final int CODE_READ_EXTERNAL_STORAGE = 0x0003;

    //Activity.startForResult
    public static final int CODE_START_ACTIVITY_FOR_RESULT = 1;

    //四张重点图
    public static final String URL_GUIDE_IMG_NET = "http://www.iyuce.com/res/images/assault.jpg";
    public static final String URL_GUIDE_IMG_FREE = "http://www.iyuce.com/res/images/tl.jpg";
    public static final String URL_GUIDE_IMG_SPEAKING = "http://www.iyuce.com/res/images/ky.jpg";
    public static final String URL_GUIDE_IMG_WRITTING = "http://www.iyuce.com/res/images/xz.jpg";

    //WebView访问的URL汇总
    public static final String URL_WEB_ZHIBO = "https://iyuce.ke.qq.com/";
    public static final String URL_WEB_LUBO = "http://store.iyuce.com/";

    //网络班跳转商城获取商品信息
    public static final String URL_GetGoods = "http://api.iyuce.com/v1/store/getactivegoods";

    //登录
    public static final String ACTIVITY_LOGIN = "login";
    public static final String ACTIVITY_LOGIN_REGISTER = "loginregister";
    public static final String ACTIVITY_LOGIN_REGISTER_INFO = "loginregisterinfo";
    public static final String ACTIVITY_LOGIN_RESET = "loginreset";
    public static final String URL_API_REQUESTTOKEN = "http://api.iyuce.com/api/token";
    public static final String URL_POST_LOGIN = "http://api.iyuce.com/v1001/account/login";
    public static final String URL_POST_LOGIN_UPLOADTIME = "http://api.iyuce.com/v1/exam/setexamtime";
    public static final String URL_POST_LOGIN_SEND_PHONE_MSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
    public static final String URL_POST_LOGIN_SEND_EMAIL_MSG = "http://api.iyuce.com/v1/common/sendemailvericode";
    public static final String URL_POST_LOGIN_WITH_MESSAGE = "http://api.iyuce.com/v1/account/smslogin";
    public static final String URL_POST_LOGIN_VERIFY_CODE = "http://api.iyuce.com/v1/common/verifycode";
    public static final String URL_POST_LOGIN_VAILD = "http://api.iyuce.com/v1/account/valid";
    public static final String URL_POST_LOGIN_ACTIVITE_EMAIL = "http://api.iyuce.com/v1/account/active_email";
    public static final String URL_POST_LOGIN_RESET_PASSWORD = "http://api.iyuce.com/v1/account/reset_password";
    public static final String URL_POST_LOGIN_REGISTER = "http://api.iyuce.com/v1/account/register";

    //口语
    public static final String ACTIVITY_SPEAKING = "speaking";
    public static final String ACTIVITY_SPEAKING_STATIS = "speakingstatis";
    public static final String ACTIVITY_SPEAKING_SEARCH = "speakingsearch";
    public static final String ACTIVITY_SPEAKING_MORE = "speakingmore";
    public static final String ACTIVITY_SPEAKING_CONTENT = "speakingcontent";
    public static final String ACTIVITY_SPEAKING_VOTE = "speakingvote";
    public static final String ACTIVITY_SPEAKING_SHARE_CHOOSE = "speakingsharechoose";
    public static final String ACTIVITY_SPEAKING_SHARE_ONE = "speakingshareone";
    public static final String FRAGMENT_SHARE_THREE = "speakingsharethree";
    public static final String ACTIVITY_SPEAKING_SHARE_FOUR = "speakingsharefour";
    public static final String URL_POST_SPEAKING = "http://iphone.ipredicting.com/getvoteMge.aspx";
    public static final String URL_POST_SPEAKING_STATIS_CITY = "http://iphone.ipredicting.com/kyCityApi.aspx";
    public static final String URL_POST_SPEAKING_STATIS_VOTE = "http://iphone.ipredicting.com/kysubOrder.aspx";
    public static final String URL_POST_SPEAKING_SEARCH = "http://iphone.ipredicting.com/kysubSearch.aspx";
    public static final String URL_POST_SPEAKIGN_MORE = "http://iphone.ipredicting.com/kysubCategoryApi.aspx";
    public static final String URL_POST_SPEAKING_CONTENT = "http://iphone.ipredicting.com/kysubContent.aspx";
    public static final String URL_POST_SPEAKING_CHOOSE_AREA = "http://iphone.ipredicting.com/kyAreaApi.aspx";
    public static final String URL_POST_SPEAKING_CHOOSE_CITY = "http://iphone.ipredicting.com/kyCityApi.aspx";
    public static final String URL_POST_SPEAKING_CHOOSE_ROOM = "http://iphone.ipredicting.com/kyRoomApi.aspx";
    public static final String URL_POST_SPEAKING_VOTE_VOTE = "http://iphone.ipredicting.com/kysubVote.aspx";
    public static final String URL_GET_SPEAKING_SHARE_ONE = "http://iphone.ipredicting.com/ksexamtime.aspx";
    public static final String URL_POST_SPEAKING_SHARE_THREE_FRAGMENT = "http://iphone.ipredicting.com/kysubNshare.aspx";
    public static final String URL_POST_SPEAKING_SHARE_FOUR = "http://iphone.ipredicting.com/kysubshare.aspx";

    //免费范围
    public static final String ACTIVITY_RANGE = "freerange";
    public static final String ACTIVITY_BOOK = "freebook";
    public static final String ACTIVITY_LESSON = "freelesson";
    public static final String ACTIVITY_PAGE = "freepage";
    public static final String ACTIVITY_CONTENT = "freecontent";
    public static final String FRAGMENT_CONTENT = "freeanswersheet";
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
    public static final String ACTIVITY_NET = "netclass";
    public static final String ACTIVITY_NET_LESSON = "netclasslesson";
    public static final String URL_POST_NET_TIME = "http://api.iyuce.com/v1/exam/notifydropdownlist";
    public static final String URL_POST_NET_NOTICE = "http://api.iyuce.com/v1/exam/notifycontent";
    public static final String URL_POST_NET_WEB_COURSE = "http://api.iyuce.com/v1/exam/webcoursegroup";
    public static final String URL_POST_NET_LESSON = "http://api.iyuce.com/v1/exam/examunit";
    public static final String URL_POST_NET_LESSON_Check = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    public static final String URL_POST_NET_LESSON_CheckCode = "http://api.iyuce.com/v1/exam/activecodeforwlb";

    //公益课堂音频
    public static final String ACTIVITY_AUDIO_LESSON = "audiolesson";
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