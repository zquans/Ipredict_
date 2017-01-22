package com.woyuce.activity.common;

public class Constants {

    public static final int CODE_CAMERE = 0x0001;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0x0002;
    public static final int CODE_READ_EXTERNAL_STORAGE = 0x0003;

    //Activity.startForResult
    public static final int CODE_START_ACTIVITY_FOR_RESULT = 1;

    //Common
    public static final String URL_MONEY_INFO = "http://api.iyuce.com/v1/store/getusermoney?userid=";
    public static final String ACTIVITY_SUGGESTION = "Suggestion";
    public static final String URL_POST_SUGGESTION = "http://api.iyuce.com/v1/service/feedback";
    public static final String URL_POST_TAB_FIVE_MY_ROOM = "http://iphone.ipredicting.com/kymyroom.aspx";
    public static final String URL_POST_TAB_FIVE_MY_SUBJECT = "http://iphone.ipredicting.com/kymyshanesub.aspx";
    public static final String URL_GetGoods = "http://api.iyuce.com/v1/store/getactivegoods";       //网络班跳转商城获取商品信息
    public static final String URL_POST_SET_EXAM_TIME = "http://api.iyuce.com/v1/exam/setexamtime";    //倒计时提醒板

    //WebView访问的URL汇总
    public static final String URL_TAB_BBS = "http://bbs.iyuce.com/bar/bbspullrefresh";
    public static final String URL_WEB_ZHIBO = "https://iyuce.ke.qq.com/";

    //四张重点导航图
    public static final String URL_GUIDE_IMG_NET = "http://www.iyuce.com/res/images/assault.jpg";
    public static final String URL_GUIDE_IMG_FREE = "http://www.iyuce.com/res/images/tl.jpg";
    public static final String URL_GUIDE_IMG_SPEAKING = "http://www.iyuce.com/res/images/ky.jpg";
    public static final String URL_GUIDE_IMG_WRITTING = "http://www.iyuce.com/res/images/xz.jpg";

    //登录
    public static final String ACTIVITY_LOGIN = "Login";
    public static final String ACTIVITY_LOGIN_REGISTER = "LoginRegister";
    public static final String ACTIVITY_LOGIN_REGISTER_INFO = "LoginRegisterInfo";
    public static final String ACTIVITY_LOGIN_RESET = "LoginReset";
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
    public static final String ACTIVITY_SPEAKING = "Speaking";
    public static final String ACTIVITY_SPEAKING_STATIS = "SpeakingStatics";
    public static final String ACTIVITY_SPEAKING_SEARCH = "SpeakingSearch";
    public static final String ACTIVITY_SPEAKING_MORE = "SpeakingMore";
    public static final String ACTIVITY_SPEAKING_CONTENT = "SpeakingContent";
    public static final String ACTIVITY_SPEAKING_VOTE = "SpeakingVote";
    public static final String ACTIVITY_SPEAKING_SHARE_CHOOSE = "SpeakingShareChoose";
    public static final String ACTIVITY_SPEAKING_SHARE_ONE = "SpeakingShareOne";
    public static final String FRAGMENT_SHARE_THREE = "SpeakingShareThree";
    public static final String ACTIVITY_SPEAKING_SHARE_FOUR = "SpeakingShareFour";
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
    public static final String ACTIVITY_RANGE = "FreeRange";
    public static final String ACTIVITY_BOOK = "FreeBook";
    public static final String ACTIVITY_LESSON = "FreeLesson";
    public static final String ACTIVITY_PAGE = "FreePage";
    public static final String ACTIVITY_CONTENT = "FreeContent";
    public static final String FRAGMENT_CONTENT = "FreeAnswerSheet";
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
    public static final String ACTIVITY_NET = "NetClass";
    public static final String ACTIVITY_NET_LESSON = "NetClassLesson";
    public static final String URL_POST_NET_TIME = "http://api.iyuce.com/v1/exam/notifydropdownlist";
    public static final String URL_POST_NET_NOTICE = "http://api.iyuce.com/v1/exam/notifycontent";
    public static final String URL_POST_NET_WEB_COURSE = "http://api.iyuce.com/v1/exam/webcoursegroup";
    public static final String URL_POST_NET_LESSON = "http://api.iyuce.com/v1/exam/examunit";
    public static final String URL_POST_NET_LESSON_Check = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    public static final String URL_POST_NET_LESSON_CheckCode = "http://api.iyuce.com/v1/exam/activecodeforwlb";

    //公益课堂音频
    public static final String ACTIVITY_AUDIO_LESSON = "AudioLesson";
    public static final String URL_GET_AUDIO_TYPE = "http://api.iyuce.com/v1/exam/audiotypes";
    public static final String URL_POST_AUDIO_LIST = "http://api.iyuce.com/v1/exam/audios";

    //写作
    public static final String ACTIVITY_WIT = "Writting";
    public static final String ACTIVITY_WIT_SEARCH = "WitSearch";
    public static final String ACTIVITY_WIT_SUBCATEGORY = "WitSubcategory";
    public static final String ACTIVITY_WIT_CONTENT = "WitContent";
    public static final String URL_POST_WRITTING_CATEGORY = "http://iphone.ipredicting.com/xzCategoryApi.aspx";
    public static final String URL_POST_WRITTING_SEARCH = "http://iphone.ipredicting.com/xzsubSearch.aspx";
    public static final String URL_POST_WRITTING_SUBCATEGORY = "http://iphone.ipredicting.com/xzsubCategory.aspx";
    public static final String URL_POST_WRITTING_TOTAL = "http://iphone.ipredicting.com/xzCategoryApi.aspx";
    public static final String URL_POST_WRITTING_CONTENT = "http://iphone.ipredicting.com/xzsubContent.aspx";

    //商城
    public static final String ACTIVITY_STORE_GOODS = "StoreGoods";
    public static final String ACTIVITY_STORE_CAR = "StoreCar";
    public static final String ACTIVITY_STORE_PAY = "StorePay";
    public static final String ACTIVITY_STORE_ADDRESS = "StoreAddress";
    public static final String ACTIVITY_STORE_ADD_ADDRESS = "StoreAddAddress";
    public static final String ACTIVITY_STORE_ORDER = "StoreOrder";
    public static final String ACTIVITY_STORE_ORDER_LIST = "StoreOrderList";
    public static final String ACTIVITY_STORE_COMMENT = "StoreComment";
    public static final String FRAGMENT_STORE_GOODS_ONE = "StoreGoodsOne";
    public static final String FRAGMENT_STORE_GOODS_THREE = "StoreGoodsThree";
    public static final String URL_GET_STORE_GOODS = "http://api.iyuce.com/v1/store/goods";
    public static final String URL_GET_STORE_PAY = "http://api.iyuce.com/v1/store/getdefaultaddr";
    public static final String URL_GET_STORE_ADDRESS = "http://api.iyuce.com/v1/store/findbyuser";
    public static final String URL_POST_STORE_ADD_ADDRESS = "http://api.iyuce.com/v1/store/OperationAddress";
    public static final String URL_GET_STORE_ORDER_LIST = "http://api.iyuce.com/v1/store/orderlist?pageSize=10&userid=";
    public static final String URL_GET_STORE_ORDER_LIST_Del = "http://api.iyuce.com/v1/store/orderdelete?userid=";
    public static final String URL_POST_STORE_COMMENT = "http://api.iyuce.com/v1/store/submitcomment";
    public static final String URL_POST_STORE_ORDER_MAKE_ORDER = "http://api.iyuce.com/v1/store/order";
    public static final String URL_POST_STORE_ORDER_TO_PAY = "http://api.iyuce.com/v1/store/pay";
    public static final String URL_POST_STORE_ORDER_TO_ALI_PAY = "http://api.iyuce.com/v1/store/paywithcash?paytype=alipay&id=";
    public static final String URL_POST_STORE_ORDER_TO_WXPAY_PAY = "http://api.iyuce.com/v1/store/paywithcash?paytype=wxapp&id=";
    public static final String URL_POST_STORE_ORDER_TO_ALI_VALID = "http://api.iyuce.com/v1/store/validpaybyapp?paytype=alipay";
    public static final String URL_GET_STORE_HOME = "http://api.iyuce.com/v1/store/homegoodslist";
    public static final String URL_GET_STORE_GOODS_THREE_COMMENT = "http://api.iyuce.com/v1/store/goodscommentsbygoodsid";
    public static final String URL_GET_STORE_GOODS_THREE_ShowOrder = "http://api.iyuce.com/v1/store/showordersbygoodsid";
}