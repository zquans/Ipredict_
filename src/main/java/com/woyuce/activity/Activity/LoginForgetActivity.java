package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
 * Created by Administrator on 2016/11/21.
 */
public class LoginForgetActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtPhone, mEdtCode, mEdtPassword, mEdtPasswordAgain;
    private Button mBtnChangeMethod, mBtnSendCode, mBtnSubmit;

    private String URL_VERIFY_CODE = "http://api.iyuce.com/v1/common/verifycode";
    private String URL_SEND_MSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
    private String URL_SEND_EMAIL = "http://api.iyuce.com/v1/common/sendemailvericode";
    private String URL_RESET_PASSWORD = "http://api.iyuce.com/v1/account/reset_password";

    private boolean change_method = true;
    private boolean isSend = false;
    private String localtoken;
    private int time_count;

    //创建一个Handler去处理倒计时事件
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mBtnSendCode.setText(msg.obj.toString() + "秒后重发");
            mBtnSendCode.setClickable(false);
            if ((int) msg.obj == 0) {
                mBtnSendCode.setText("发送验证码");
                time_count = 61;
                mBtnSendCode.setClickable(true);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("forgetActivityRequest");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginforget);

        initView();
    }

    private void initView() {
        mEdtPhone = (EditText) findViewById(R.id.edt_activity_loginforget_phone);
        mEdtCode = (EditText) findViewById(R.id.edt_activity_loginforget_phone);
        mEdtPassword = (EditText) findViewById(R.id.edt_activity_loginforget_phone);
        mEdtPasswordAgain = (EditText) findViewById(R.id.edt_activity_loginforget_phone);
        mBtnChangeMethod = (Button) findViewById(R.id.btn_activity_loginforget_changemethod);
        mBtnSendCode = (Button) findViewById(R.id.btn_activity_loginforget_sendcode);
        mBtnSubmit = (Button) findViewById(R.id.btn_activity_loginforget_submit);

        mBtnChangeMethod.setOnClickListener(this);
        mBtnSendCode.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    /**
     * 请求验证码
     */
    private void toSendCode(String url) {
        StringRequest forgetRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject obj;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        isSend = true;
                        if (change_method) {
                            ToastUtil.showMessage(LoginForgetActivity.this, "已发送，请查看短信哦,亲!");
                        } else {
                            ToastUtil.showMessage(LoginForgetActivity.this, "已发送，请查看邮件哦,亲!");
                        }
                        //倒计时
                        toCount();
                    } else {
                        ToastUtil.showMessage(LoginForgetActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginForgetActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                if (change_method) {
                    map.put("phone", mEdtPhone.getText().toString().trim());
                } else {
                    map.put("email", mEdtPhone.getText().toString().trim());
                }
                map.put("template", "VeriCode");
                return map;
            }
        };
        forgetRequest.setTag("forgetActivityRequest");
        AppContext.getHttpQueue().add(forgetRequest);
    }

    /**
     * 验证验证码
     * 通过后请求修改密码
     */
    private void toSubmit() {
        StringRequest CheckRequest = new StringRequest(Request.Method.POST, URL_VERIFY_CODE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    LogUtil.i("what? obj = " + obj);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(LoginForgetActivity.this, "发送一个请求去修改密码");
                        resetPassword();
                    } else {
                        ToastUtil.showMessage(LoginForgetActivity.this, "验证失败，请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-Back", "连接错误原因： " + error.getMessage());
                ToastUtil.showMessage(LoginForgetActivity.this, "网络错误，请重试");
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
                map.put("email_or_mobile", mEdtPhone.getText().toString().trim());
                map.put("code", mEdtCode.getText().toString().trim());
                return map;
            }
        };
        CheckRequest.setTag("forgetActivityRequest");
        AppContext.getHttpQueue().add(CheckRequest);
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        StringRequest resetRequest = new StringRequest(Request.Method.POST, URL_RESET_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    LogUtil.i("what? = " + obj);
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
                map.put("key", mEdtPhone.getText().toString().trim());
                map.put("value", mEdtPassword.getText().toString().trim());
                return map;
            }
        };
        resetRequest.setTag("forgetActivityRequest");
        AppContext.getHttpQueue().add(resetRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_loginforget_submit:
                if (!isSend) {
                    ToastUtil.showMessage(this, "请先获取验证码");
                    break;
                }
                if (!mEdtPassword.getText().toString().equals(mEdtPasswordAgain.getText().toString())) {
                    ToastUtil.showMessage(this, "两次输入的密码不一致哦");
                    break;
                }
                if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())
                        || mEdtPhone.getText().toString().trim().equals("")
                        || TextUtils.isEmpty(mEdtCode.getText().toString().trim())
                        || TextUtils.isEmpty(mEdtPassword.getText().toString().trim())
                        || TextUtils.isEmpty(mEdtPasswordAgain.getText().toString().trim())
                        ) {
                    ToastUtil.showMessage(this, "请检查内容是否填充完整");
                    break;
                }
                toSubmit();
                break;
            case R.id.btn_activity_loginforget_sendcode:
                if (change_method)
                    toSendCode(URL_SEND_MSG);
                else
                    toSendCode(URL_SEND_EMAIL);
                break;
            case R.id.btn_activity_loginforget_changemethod:
                if (change_method) {
                    mBtnChangeMethod.setText("切换为电话找回");
                    mEdtPhone.setText("");
                    mEdtPhone.setHint("请输入电子邮箱地址");
                    mEdtPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                    mEdtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                } else {
                    mBtnChangeMethod.setText("切换为邮箱找回");
                    mEdtPhone.setText("");
                    mEdtPhone.setHint("请输入手机号码");
                    mEdtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    mEdtPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                change_method = !change_method;
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