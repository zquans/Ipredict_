package com.woyuce.activity.Act.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Act.BaseActivity;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/22.
 */
public class LoginRegisterActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtback;
    private EditText mEdtPhonenum, mEdtAcceptMsg;
    private Button btnSend, btnToNext;
    private String localtoken, localChecknum;

    //上一级传来，用于判断是短信还是邮件
    private String email_or_phone, forget_password, activity_email;

    private String URL_SEND_PHONE_MSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
    private String URL_SEND_EMAIL_MSG = "http://api.iyuce.com/v1/common/sendemailvericode";
    private String URL_TONEXT = "http://api.iyuce.com/v1/common/verifycode";
    private String URL_ACTIVITY_EMAIL = "http://api.iyuce.com/v1/account/active_email";
    private String URL_VAILD = "http://api.iyuce.com/v1/account/valid";

    private int time_count;
    private boolean isVarify = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginRegisterActivity.this, LoginActivity.class));
        finish();
    }

    //创建一个Handler去处理倒计时事件
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            btnSend.setText(msg.obj.toString() + "秒后重发");
            btnSend.setClickable(false);
            if ((int) msg.obj == 0) {
                btnSend.setText("发送验证码");
                time_count = 61;
                btnSend.setClickable(true);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("register");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);

        initView();
    }

    private void initView() {
        email_or_phone = getIntent().getStringExtra("environment");
        forget_password = getIntent().getStringExtra("method");
        activity_email = getIntent().getStringExtra("action");

        txtback = (TextView) findViewById(R.id.txt_register_back);
        mEdtPhonenum = (EditText) findViewById(R.id.edit_register_acceptphonenum);
        mEdtAcceptMsg = (EditText) findViewById(R.id.edit_register_acceptmsg);

        btnSend = (Button) findViewById(R.id.btn_register_sendmsg);
        btnToNext = (Button) findViewById(R.id.btn_register_tonext);

        txtback.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnToNext.setOnClickListener(this);

        if (!TextUtils.isEmpty(forget_password)) {
            txtback.setText("找回密码1/2");
        }
        if (!TextUtils.isEmpty(activity_email)) {
            txtback.setText("激活邮箱");
            btnToNext.setText("激活邮箱");
        }
        if (!TextUtils.isEmpty(email_or_phone)) {
            if (email_or_phone.equals("phone")) {
                mEdtPhonenum.setHint("请输入手机号码");
                mEdtPhonenum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                mEdtPhonenum.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
    }

    /**
     * 检查电话或邮箱是否可用
     */
    private void RequestVaild() {
        StringRequest VaildRequest = new StringRequest(Request.Method.POST, URL_VAILD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("response = " + response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
                        String localdata = obj.getString("data");
                        if (localdata.equals("1")) {
                            if (email_or_phone.equals("phone")) {
                                RequestMsg(URL_SEND_PHONE_MSG);
                            } else {
                                RequestMsg(URL_SEND_EMAIL_MSG);
                            }
                        } else {
                            ToastUtil.showMessage(LoginRegisterActivity.this, obj.getString("message"));
                        }
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginRegisterActivity.this, "网络错误，请稍候再试");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginRegisterActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                if (email_or_phone.equals("phone")) {
                    map.put("key", "mobile");
                } else {
                    map.put("key", "email");
                }
                map.put("value", mEdtPhonenum.getText().toString().trim());
                return map;
            }
        };
        VaildRequest.setTag("register");
        AppContext.getHttpQueue().add(VaildRequest);
    }

    /**
     * 发送验证码
     */
    private void RequestMsg(String url) {
        progressdialogshow(this);
        isVarify = true;
        StringRequest MsgRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("response 2 = " + response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("code").equals("0")) {
                        if (!TextUtils.isEmpty(activity_email)) {
                            ToastUtil.showMessage(LoginRegisterActivity.this, "已发送，请查看邮件哦,亲!");
                        } else {
                            if (email_or_phone.equals("phone")) {
                                ToastUtil.showMessage(LoginRegisterActivity.this, "已发送，请查看短信哦,亲!");
                            } else {
                                ToastUtil.showMessage(LoginRegisterActivity.this, "已发送，请查看邮件哦,亲!");
                            }
                        }
                        //倒计时
                        toCount();
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, obj.getString("message"));
                    }
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginRegisterActivity.this, "网络错误，请稍候再试");
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginRegisterActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                if (!TextUtils.isEmpty(activity_email)) {
                    map.put("email", mEdtPhonenum.getText().toString().trim());
                } else {
                    if (email_or_phone.equals("phone")) {
                        map.put("phone", mEdtPhonenum.getText().toString().trim());
                    } else {
                        map.put("email", mEdtPhonenum.getText().toString().trim());
                    }
                }
                map.put("template", "VeriCode");
                return map;
            }
        };
        MsgRequest.setTag("register");
        AppContext.getHttpQueue().add(MsgRequest);
    }

    /**
     * 验证验证码
     */
    private void requeToNext(final String arg) {
        StringRequest CheckRequest = new StringRequest(Request.Method.POST, URL_TONEXT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "验证成功啦,亲!");
                        if (arg.equals("forget_password")) {
                            Intent intent = new Intent(LoginRegisterActivity.this, LoginResetActivity.class);
                            intent.putExtra("local_phone_or_email", mEdtPhonenum.getText().toString().trim());
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginRegisterActivity.this, LoginRegisterInfoActivity.class);
                            intent.putExtra("email_or_phone", email_or_phone);
                            intent.putExtra("local_phone_or_email", mEdtPhonenum.getText().toString().trim());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "验证失败，请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginRegisterActivity.this, "验证失败，请重试");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email_or_mobile", mEdtPhonenum.getText().toString().trim());
                map.put("code", localChecknum);
                return map;
            }
        };
        CheckRequest.setTag("register");
        AppContext.getHttpQueue().add(CheckRequest);
    }

    /**
     * 激活邮箱
     */
    private void requeActivityEmail(final String key, final String value) {
        StringRequest activityEmailRequest = new StringRequest(Request.Method.POST, URL_ACTIVITY_EMAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i("obj = " + response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("code").equals("0")) {
                        new AlertDialog.Builder(LoginRegisterActivity.this)
                                .setMessage(obj.getString("message"))
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LoginRegisterActivity.this.finish();
                                    }
                                }).show();
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginRegisterActivity.this, "验证失败，请重试");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", key);
                map.put("value", value);
                return map;
            }
        };
        activityEmailRequest.setTag("register");
        AppContext.getHttpQueue().add(activityEmailRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_register_back:
                startActivity(new Intent(LoginRegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btn_register_sendmsg:
                //激活邮箱
                if (!TextUtils.isEmpty(activity_email)) {
                    if (TextUtils.isEmpty(mEdtPhonenum.getText().toString())) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "请输入邮箱地址");
                        break;
                    }
                    if (!mEdtPhonenum.getText().toString().contains("@")) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "请输入正确的邮箱地址");
                        break;
                    }
                    RequestMsg(URL_SEND_EMAIL_MSG);
                    break;
                }
                //发送短信或者邮件
                if (TextUtils.isEmpty(mEdtPhonenum.getText().toString().trim())) {
                    if (email_or_phone.equals("phone")) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "请输入手机号码");
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "请输入邮箱地址");
                    }
                    break;
                }
                if (TextUtils.isEmpty(forget_password)) {
                    RequestVaild();
                } else {
                    //如果是找回密码，则直接发送验证短信、邮件，不用去判断是否唯一
                    if (email_or_phone.equals("phone")) {
                        RequestMsg(URL_SEND_PHONE_MSG);
                    } else {
                        RequestMsg(URL_SEND_EMAIL_MSG);
                    }
                }
                break;
            case R.id.btn_register_tonext:
                if (!isVarify) {
                    ToastUtil.showMessage(this, "请先进行验证");
                    break;
                }
                localChecknum = mEdtAcceptMsg.getText().toString().trim();
                //激活邮箱
                if (!TextUtils.isEmpty(activity_email)) {
                    String key = mEdtPhonenum.getText().toString().trim();
                    String value = localChecknum;
                    requeActivityEmail(key, value);
                    break;
                }
                //是找回密码则进入该if，否则就是正常注册
                if (!TextUtils.isEmpty(forget_password)) {
                    requeToNext("forget_password");
                    break;
                }
                requeToNext("to_validate");
                break;
        }
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
}