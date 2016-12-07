package com.woyuce.activity.Act;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.Utils.UpdateManager;
import com.woyuce.activity.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private Button btninto, btnLogin, btnRegister, btnApply;
    private EditText edtUsername, edtPassword;
    private TextView txtForget;
    private ImageView imgEye;

    private String strPassword, strUserName; // 本类中变量，用于下次登录时作自动登录的数据
    private String localtoken;
    private String LOGIN_URL = "http://api.iyuce.com/v1001/account/login";
    private String URL_UPLOADTIME = "http://api.iyuce.com/v1/exam/setexamtime";

    // 注册页面跳转过来用
    private String username_register, password_register, timer_register;

    //密码是否可见
    private boolean isEyeCan = false;

    //声明推送管理
    private PushAgent mPushAgent;

    //设备唯一标识码
    private String local_push_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
        localtoken = PreferenceUtil.getSharePre(LoginActivity.this).getString("localtoken", "");

        if (TextUtils.isEmpty(localtoken)) {
            getBaseToken();
        } else {
            LogUtil.e("localtoken2 = " + localtoken);
        }

        //判断是否有权限
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            doUpdate();
        } else {
            requestPermission(Constants.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

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
        AppContext.getHttpQueue().cancelAll("login");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(local_push_code)) {
            if (local_push_code.equals(PreferenceUtil.getSharePre(this).getString("userId", "default"))) {
                if (!local_push_code.equals("default")) {
                    new AlertDialog.Builder(this).setTitle("您的账号在别处异常登录")
                            .setMessage("如果不是本人操作，请及时修改密码")
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

        btninto = (Button) findViewById(R.id.btn_login);
        btnLogin = (Button) findViewById(R.id.btn_loginAtOnce);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnApply = (Button) findViewById(R.id.btn_apply);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        txtForget = (TextView) findViewById(R.id.txt_activity_login_forget);
        imgEye = (ImageView) findViewById(R.id.img_login_eye);

        imgEye.setOnClickListener(this);
        btninto.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        txtForget.setOnClickListener(this);

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
        //TODO 如果设备ID为null，return
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
     *
     * @param mTime
     */
    private void uploadTime(final String mTime, final String userid) {
        StringRequest strinRequest = new StringRequest(Method.POST, URL_UPLOADTIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    String code = obj.getString("code");
                    if (code.equals("0")) {
                        LogUtil.e("settime,success");
                    } else {
                        LogUtil.e("settime,false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", userid);
                map.put("exam_time", mTime);
                return map;
            }
        };
        strinRequest.setTag("login");
        AppContext.getHttpQueue().add(strinRequest);
    }

    /**
     * 请求登录
     */
    private void loginRequest() {
        progressdialogshow(this);
        StringRequest stringRequest = new StringRequest(Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("response + time = " + response);
                JSONObject jsonObject;
                try {
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
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialogcancel();
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginActivity.this, "登录超时，请重试");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", strUserName);
                map.put("password", strPassword);
                map.put("deviceid", AppContext.getDeviceToken());
                return map;
            }
        };
        stringRequest.setTag("login");
        AppContext.getHttpQueue().add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
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
                break;
            case R.id.btn_loginAtOnce:
                startActivity(new Intent(this, MainActivity.class));
                PreferenceUtil.removeall(this); // 只留下了版本号
//                CookieManager.getInstance().removeAllCookie();
                // PreferenceUtil.clear(this);
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
//                startActivity(new Intent(this, LoginForgetActivity.class));
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
}