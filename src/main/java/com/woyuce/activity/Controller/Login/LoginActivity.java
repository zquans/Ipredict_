package com.woyuce.activity.Controller.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Login.Third.LoginThirdChoiceActivity;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.Controller.WebActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ActivityManager;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.Utils.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity implements OnClickListener {

    /**
     * creat_at 2016/12/20
     * 用于做界面手机登录
     */
    private LinearLayout mLinearLayoutChooseOne, mLinearLayoutChooseTwo;
    private TextView mTxtChooseOne, mTxtChooseTwo;

    private Button btninto, btnLogin, btnRegister, btnApply, btnGetCode;
    private EditText edtUsername, edtPassword, edtMobile, edtValidateCode;
    private TextView txtForget, txtActivityEmail;
    private ImageView imgEye;

    private String strPassword, strUserName; // 本类中变量，用于下次登录时作自动登录的数据
    private String localtoken;
    //    private String LOGIN_URL = "http://api.iyuce.com/v1001/account/login";
//    private String URL_UPLOADTIME = "http://api.iyuce.com/v1/exam/setexamtime";
//    private String URL_SEND_PHONE_MSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
//    private String URL_LOGIN_WITH_MESSAGE = "http://api.iyuce.com/v1/account/smslogin";

    // 注册页面跳转过来用
    private String username_register, password_register, timer_register;

    //密码是否可见
    private boolean isEyeCan = false;

    //声明推送管理
    private PushAgent mPushAgent;

    //设备唯一标识码
    private String local_push_code;

    //选择登录方式
    private boolean login_with_mobile = false;

    //保存倒计时计数
    private int time_count;
    //创建一个Handler去处理倒计时事件
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            btnGetCode.setText(msg.obj.toString() + "秒后重发");
            btnGetCode.setClickable(false);
            if ((int) msg.obj == 0) {
                btnGetCode.setText("发送验证码");
                time_count = 61;
                btnGetCode.setClickable(true);
            }
        }
    };

    //第三方登录
    private static final int MSG_AUTH_CANCEL = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_COMPLETE = 3;
    private Handler mThirdLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUTH_COMPLETE:
                    giveLoginMsgToBack((Platform) msg.obj);
                    break;
                case MSG_AUTH_ERROR:
                    ToastUtil.showMessage(LoginActivity.this, "网络错误，请重试");
                    break;
                case MSG_AUTH_CANCEL:
                    ToastUtil.showMessage(LoginActivity.this, "您取消了授权");
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //修复强踢可返回Bug
        ActivityManager.getAppManager().finishAllActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);

        initView();

        long expires_time = PreferenceUtil.getSharePre(LoginActivity.this).getLong("expires_time", 0);
        long current_time = System.currentTimeMillis();
        LogUtil.e("expires_time = " + expires_time + "||| current_time = " + current_time);
        if (current_time > expires_time) {
            getBaseToken();
        } else {
            localtoken = PreferenceUtil.getSharePre(LoginActivity.this).getString("localtoken", "");
        }
        LogUtil.e("localtoken = " + localtoken);

        //判断是否有权限
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            doUpdate();
        } else {
            requestPermission(Constants.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        //推送初始化
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
    }

    @Override
    public void doUpdate() {
        new UpdateManager(this).checkUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        local_push_code = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_LOGIN);
    }

    /**
     * 推送处理,强制登出通知
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(local_push_code)) {
            if (local_push_code.equals(PreferenceUtil.getSharePre(this).getString("userId", "default"))) {
                if (!local_push_code.equals("default")) {
                    new AlertDialog.Builder(this).setTitle("通知:")
                            .setMessage(getIntent().getStringExtra("local_push_message"))
                            .setPositiveButton("知道了", null)
                            .show();
                    LogUtil.e("----" + local_push_code + "------" + AppContext.getDeviceToken());

                    local_push_code = null;
                }
            }
        }
    }

    private void initView() {
        Intent intent = getIntent();
        username_register = intent.getStringExtra("username_register");
        password_register = intent.getStringExtra("password_register");
        timer_register = intent.getStringExtra("timer_register");
        local_push_code = intent.getStringExtra("local_push_code");

        mLinearLayoutChooseOne = (LinearLayout) findViewById(R.id.ll_activity_choose_one);
        mLinearLayoutChooseTwo = (LinearLayout) findViewById(R.id.ll_activity_choose_two);
        mTxtChooseOne = (TextView) findViewById(R.id.txt_activity_login_choose_one);
        mTxtChooseTwo = (TextView) findViewById(R.id.txt_activity_login_choose_two);
        edtMobile = (EditText) findViewById(R.id.edt_mobile);
        edtValidateCode = (EditText) findViewById(R.id.edt_mobile_validate_code);
        btnGetCode = (Button) findViewById(R.id.btn_actvity_login_get_code);
        mTxtChooseOne.setOnClickListener(this);
        mTxtChooseTwo.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);

        btninto = (Button) findViewById(R.id.btn_login);
        btnLogin = (Button) findViewById(R.id.btn_loginAtOnce);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnApply = (Button) findViewById(R.id.btn_apply);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        txtForget = (TextView) findViewById(R.id.txt_activity_login_forget);
        txtActivityEmail = (TextView) findViewById(R.id.txt_activity_login_active_email);
        imgEye = (ImageView) findViewById(R.id.img_login_eye);

        imgEye.setOnClickListener(this);
        btninto.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        txtForget.setOnClickListener(this);
        txtActivityEmail.setOnClickListener(this);

        // 给Edit输入默认账号密码
        if (!TextUtils.isEmpty(username_register)) {
            edtUsername.setText(username_register);
            edtPassword.setText(password_register);
        } else {
            edtUsername.setText(PreferenceUtil.getSharePre(this).getString("username", ""));
            edtPassword.setText(PreferenceUtil.getSharePre(this).getString("password", ""));
        }
        LogUtil.e("username_register = " + username_register + "-----" + password_register);
    }

    /**
     * 设置推送标签
     */
    private void addTag(String tag) {
        mPushAgent.getTagManager().add(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
                if (isSuccess) {
                    LogUtil.i("Add Tag:" + result);
                } else {
                    LogUtil.i("Add Tag:" + "加入tag失败");
                }
            }
        }, tag);
    }

    /**
     * 设置推送别名
     */
    private void addAlias(String userid) {
        //获取设备唯一IMEI码
//        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        String user_device_token = TelephonyMgr.getDeviceId();

        //别名和分组(别名应该用user_id)
        String alias = userid;
        String aliasType = "user_id";
        // 如果设备ID为null，return
        LogUtil.i("device_token = " + alias);
        mPushAgent.addAlias(alias, aliasType, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.i("isSuccess:" + isSuccess + "," + message);
                if (isSuccess)
                    LogUtil.i("alias was set successfully.");

                final boolean success = isSuccess;
                LogUtil.i("Add Alias:" + (success ? "Success" : "Fail"));
            }
        });
    }

    /**
     * 将注册中传过来的考试时间上传
     */
    private void uploadTime(final String mTime, final String userid) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userid);
        params.put("exam_time", mTime);
        HttpUtil.post(Constants.URL_POST_LOGIN_UPLOADTIME, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        LogUtil.e("settime,success");
                    } else {
                        LogUtil.e("settime,false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取基本请求token
     */
    public void getBaseToken() {
        String base64EncodedString = null;
        try {
            String encodedConsumerKey = URLEncoder.encode("defA8Dq2ambB", "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode("WM7Ei5mzrrHl42HHXuGkNR0bVJexq4P", "UTF-8");
            String authString = encodedConsumerKey + ":" + encodedConsumerSecret;
            base64EncodedString = Base64.encodeToString(authString.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + base64EncodedString);
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("scope", "");
        HttpUtil.post(Constants.URL_API_REQUESTTOKEN, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    localtoken = obj.getString("access_token");
                    String expires_in = obj.getString("expires_in");
                    long current_time = System.currentTimeMillis();
                    long expires_time = current_time + Long.parseLong(expires_in) * 1000;
                    PreferenceUtil.save(LoginActivity.this, "localtoken", localtoken);
                    PreferenceUtil.save(LoginActivity.this, "expires_time", expires_time);
                    //存一个时间
                    LogUtil.e("current_time = " + current_time + "|||" + expires_time + "|||localtoken1 = " + localtoken + "||| expires_in = " + expires_in);
                    LogUtil.e("token ==== " + PreferenceUtil.getSharePre(LoginActivity.this).getString("localtoken", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 请求用户名登录
     */
    private void loginRequest() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("username", strUserName);
        params.put("password", strPassword);
        params.put("deviceid", AppContext.getDeviceToken());
        HttpUtil.post(Constants.URL_POST_LOGIN, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                onSuccess(result);
            }
        });
    }

    /**
     * 处理成功获取的JSON
     */
    private void onSuccess(String response) {
        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                ToastUtil.showMessage(LoginActivity.this, "登陆成功");
                jsonObject = jsonObject.getJSONObject("data");
                // 拿出所有返回数据
                String userId = jsonObject.getString("userid");
                String mUserName = jsonObject.getString("username");
                String Permission = jsonObject.getString("permission");
                String money = jsonObject.getString("tradepoints");
                String update = jsonObject.getString("login_time");
                String localtimer = jsonObject.getString("exam_time");
                // 如果为默认初试时间，则设为""
                if (localtimer.equals("0001-01-01")) {
                    localtimer = "";
                    // 如果注册时本地时间不为null,则将该时间赋给mtimer，并保存
                    if (!TextUtils.isEmpty(timer_register)) {
                        localtimer = timer_register;
                    }
                }
                // 将所有数据保存到sharepreferences数据库中
                PreferenceUtil.save(LoginActivity.this, "userId", userId);
                PreferenceUtil.save(LoginActivity.this, "mUserName", mUserName);
                PreferenceUtil.save(LoginActivity.this, "Permission", Permission);
                PreferenceUtil.save(LoginActivity.this, "money", money);
                PreferenceUtil.save(LoginActivity.this, "update", update);
                PreferenceUtil.save(LoginActivity.this, "mtimer", localtimer);
                // 拿到JSON中的token 打印出来
                LogUtil.e("所有数据 " + userId + "->" + mUserName + "->" + Permission + "->" + money + "->");
                /*将userId作为推送的分组别名*/
//                        addAlias(userId);
                /*将userId作为推送的分组标签*/
//                        addTag(userId);
                // 取消加载对话框
                progressdialogcancel();
                // 上传注册时设定的考试时间
//                        uploadTime(timer_register, userId);
                // 启动主页面
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                progressdialogcancel();
                ToastUtil.showMessage(LoginActivity.this, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求手机号登录
     */
    private void loginWithMessageRequest(final String mobile, final String validatecode) {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("accountmobile", mobile);
        params.put("smsvalidcode", validatecode);
        params.put("deviceid", AppContext.getDeviceToken());
        HttpUtil.post(Constants.URL_POST_LOGIN_WITH_MESSAGE, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                onSuccess(result);
            }
        });
    }

    /**
     * 发送验证码
     */
    private void RequestMsg() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", edtMobile.getText().toString().trim());
        params.put("template", "VeriCode");
        HttpUtil.post(Constants.URL_POST_LOGIN_SEND_PHONE_MSG, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        //倒计时
                        toCount();
                    } else {
                        ToastUtil.showMessage(LoginActivity.this, obj.getString("message"));
                    }
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //判断两种登录情况,1、手机号登录，
                if (login_with_mobile) {
                    String mobile = edtMobile.getText().toString();
                    String mobile_validate_code = edtValidateCode.getText().toString();
                    if (TextUtils.isEmpty(mobile_validate_code)) {
                        ToastUtil.showMessage(LoginActivity.this, "验证码不能为空");
                        return;
                    }
                    loginWithMessageRequest(mobile, mobile_validate_code);
                }
                //2、用户名登录
                if (!login_with_mobile) {
                    strUserName = edtUsername.getText().toString();
                    strPassword = edtPassword.getText().toString();
                    // 保存账号信息到sharepreferences数据库中
                    PreferenceUtil.save(LoginActivity.this, "username", LoginActivity.this.strUserName);
                    PreferenceUtil.save(LoginActivity.this, "password", LoginActivity.this.strPassword);
                    if (TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strPassword)) {
                        ToastUtil.showMessage(LoginActivity.this, "账号密码不能为空，试试体验登陆吧");
                        return;
                    }
                    //请求登录
                    loginRequest();
//                CookieManager.getInstance().removeAllCookie();
                }
                break;
            case R.id.btn_loginAtOnce:
                startActivity(new Intent(this, MainActivity.class));
                PreferenceUtil.removeall(this); // 只留下了版本号和localtoken
                finish();
                break;
            case R.id.btn_apply:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("URL", "http://www.iyuce.com/m/appfbbsq");
                intent.putExtra("TITLE", "集训营申请");
                intent.putExtra("COLOR", "#f7941d");
                startActivity(intent);
                break;
            case R.id.txt_activity_login_forget:
                final Intent intent_loginforget = new Intent(this, LoginRegisterActivity.class);
                intent_loginforget.putExtra("method", "forget_password");
                doAlertDialog(intent_loginforget, "请选择找回方式", "国内请选择手机找回，国外请选择邮箱找回", "手机找回", "邮箱找回");
                break;
            case R.id.txt_activity_login_active_email:
                Intent intent_activity_email = new Intent(this, LoginRegisterActivity.class);
                intent_activity_email.putExtra("action", "activity_email");
                startActivity(intent_activity_email);
                break;
            case R.id.btn_register:
                final Intent intent_loginregister = new Intent(this, LoginRegisterActivity.class);
                doAlertDialog(intent_loginregister, "请选择注册环境", "国内请选择手机注册，国外请选择邮箱注册", "手机注册", "邮箱注册");
                break;
            case R.id.img_login_eye:
                //登录密码是否可见
                if (!isEyeCan) {
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgEye.setBackgroundResource(R.mipmap.icon_eye_cannot);
                    isEyeCan = !isEyeCan;
                } else {
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    imgEye.setBackgroundResource(R.mipmap.icon_eye_can);
                    isEyeCan = !isEyeCan;
                }
                break;
            case R.id.txt_activity_login_choose_one:
                login_with_mobile = false;
                mLinearLayoutChooseOne.setVisibility(View.VISIBLE);
                mLinearLayoutChooseTwo.setVisibility(View.GONE);
                mTxtChooseOne.setTextColor(Color.parseColor("#ffffff"));
                mTxtChooseTwo.setTextColor(Color.parseColor("#329fe9"));
                mTxtChooseTwo.setBackgroundColor(Color.parseColor("#00000000"));
                mTxtChooseOne.setBackgroundColor(Color.parseColor("#329fe9"));
                break;
            case R.id.txt_activity_login_choose_two:
                login_with_mobile = true;
                mLinearLayoutChooseTwo.setVisibility(View.VISIBLE);
                mLinearLayoutChooseOne.setVisibility(View.GONE);
                mTxtChooseOne.setTextColor(Color.parseColor("#329fe9"));
                mTxtChooseTwo.setTextColor(Color.parseColor("#ffffff"));
                mTxtChooseOne.setBackgroundColor(Color.parseColor("#00000000"));
                mTxtChooseTwo.setBackgroundColor(Color.parseColor("#329fe9"));
                break;
            case R.id.btn_actvity_login_get_code:
                ToastUtil.showMessage(this, "获取验证码");
                if (TextUtils.isEmpty(edtMobile.getText().toString())) {
                    ToastUtil.showMessage(this, "手机号不能为空哦");
                    return;
                }
                //请求验证码
                RequestMsg();
                break;
        }
    }

    /**
     * 做弹窗选择
     */
    private void doAlertDialog(final Intent intent, String title, String message, String btn_positon, String btn_negative) {
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btn_positon, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("environment", "phone");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(btn_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("environment", "email");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNeutralButton("取消", null)
                .show();
    }

    /**
     * 倒计时
     */
    private void toCount() {
        time_count = 60;
        final Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time_count > 0 && time_count < 61) {
                    time_count = time_count - 1;
                    Message msg = Message.obtain();
                    msg.obj = time_count;
                    mHandler.sendMessage(msg);
                } else {
                    mTimer.cancel();
                }
            }
        }, 500, 1000);
    }

    /**
     * 第三方登录
     */
    public void wechatLogin(View view) {
        doShareLogin("wechat");
    }

    public void qqLogin(View view) {
        doShareLogin("qq");
    }

    private void doShareLogin(String arg) {
        progressdialogshow(this);
        ShareSDK.initSDK(this);
        Platform mplatform;
        if (arg.equals("wechat")) {
            mplatform = ShareSDK.getPlatform(this, Wechat.NAME);
        } else {
            mplatform = ShareSDK.getPlatform(this, QQ.NAME);
        }
        if (mplatform.isAuthValid()) {
            //已授权，将获取到的信息传给后台
            giveLoginMsgToBack(mplatform);
            return;
        }
        mplatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                progressdialogcancel();
                Message msg = new Message();
                msg.what = MSG_AUTH_COMPLETE;
                msg.obj = platform;
                mThirdLoginHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                progressdialogcancel();
                Message msg = new Message();
                msg.what = MSG_AUTH_ERROR;
                mThirdLoginHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                progressdialogcancel();
                Message msg = new Message();
                msg.what = MSG_AUTH_CANCEL;
                mThirdLoginHandler.sendMessage(msg);
            }
        });
        mplatform.authorize();
    }

    /**
     * 第三方登录给后台发用户信息
     */
    private void giveLoginMsgToBack(final Platform platform) {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("accounttypekey", platform.getDb().getPlatformNname());
        params.put("openid", platform.getDb().getUserId());
        params.put("unionid", TextUtils.isEmpty(platform.getDb().get("unionid")) ? platform.getDb().getUserId() : platform.getDb().get("unionid"));
        params.put("accesstoken", platform.getDb().getToken());
        params.put("expiresin", platform.getDb().getExpiresIn() + "");
        params.put("deviceid", AppContext.getDeviceToken());
        HttpUtil.post(Constants.URL_Login_To_Third, headers, params, "login", new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                progressdialogcancel();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        if (obj.getString("firstlogintothird").equals("1")) {
                            //第一次第三方登陆，选择绑定、注册新用户、或者默认生成账户登录
                            startToThirdActivity(platform);
                        } else {
                            //不是第一次登陆，直接进入下一个界面
                            obj = new JSONObject(obj.getString("data"));
                            PreferenceUtil.save(LoginActivity.this, "userId", obj.getString("userid"));
                            PreferenceUtil.save(LoginActivity.this, "mUserName", obj.getString("username"));
                            PreferenceUtil.save(LoginActivity.this, "Permission", obj.getString("permission"));
                            PreferenceUtil.save(LoginActivity.this, "money", obj.getString("tradepoints"));
                            PreferenceUtil.save(LoginActivity.this, "update", obj.getString("login_time"));
                            PreferenceUtil.save(LoginActivity.this, "mtimer", obj.getString("exam_time"));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 跳转第三方页面
     */
    private void startToThirdActivity(Platform platform) {
        Intent intent = new Intent(this, LoginThirdChoiceActivity.class);
        intent.putExtra("type", platform.getDb().getPlatformNname());
        intent.putExtra("openId", platform.getDb().getUserId());
        intent.putExtra("unionid", platform.getDb().get("unionid"));
        intent.putExtra("accessToken", platform.getDb().getToken());
        intent.putExtra("expiresin", platform.getDb().getExpiresIn() + "");
        intent.putExtra("userIcon", platform.getDb().getUserIcon());
        intent.putExtra("userGender", platform.getDb().getUserGender());
        intent.putExtra("userName", platform.getDb().getUserName());
        intent.putExtra("deviceid", AppContext.getDeviceToken());
        startActivity(intent);
    }
}