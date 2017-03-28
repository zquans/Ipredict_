package com.woyuce.activity.UI.Activity.Login;

import android.content.Intent;
import android.os.Bundle;
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
import com.woyuce.activity.AppContext;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginRegisterThirdActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTxtBack;
    private EditText mEdtPassword, mEdtRepassword, mEdtUsername, mEdtName, mEdtEmail, mEdtPhone;
    private Button mBtnCheckUserName, mBtnCommit;

    private String localtoken, type, openId, unionid, accessToken, expiresin, userIcon, userGender, userName, deviceid;
    private static final String Tag_Volley = "LoginRegisterThird";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginRegisterThirdActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getHttpQueue().cancelAll(Tag_Volley);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_third);

        initView();
    }

    private void initView() {
        type = getIntent().getStringExtra("type");
        openId = getIntent().getStringExtra("openId");
        unionid = getIntent().getStringExtra("unionid");
        accessToken = getIntent().getStringExtra("accessToken");
        expiresin = getIntent().getStringExtra("expiresin");
        userIcon = getIntent().getStringExtra("userIcon");
        userGender = getIntent().getStringExtra("userGender");
        userName = getIntent().getStringExtra("userName");
        deviceid = getIntent().getStringExtra("deviceid");
        mTxtBack = (TextView) findViewById(R.id.txt_registerinfo_back);
        mEdtUsername = (EditText) findViewById(R.id.edt_registerinfo_username);
        mEdtPassword = (EditText) findViewById(R.id.edt_registerinfo_password);
        mEdtRepassword = (EditText) findViewById(R.id.edt_registerinfo_repassword);
        mEdtName = (EditText) findViewById(R.id.edt_registerinfo_name);
        mEdtEmail = (EditText) findViewById(R.id.edt_registerinfo_email);
        mEdtPhone = (EditText) findViewById(R.id.edt_registerinfo_phone);
        mBtnCheckUserName = (Button) findViewById(R.id.btn_registerinfo_checkusername);
        mBtnCommit = (Button) findViewById(R.id.btn_registerinfo_tonext);

        mEdtUsername.setText(userName);
        mTxtBack.setOnClickListener(this);
        mBtnCheckUserName.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
    }

    private void RequestToCommit() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_Login_Third_Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ToastUtil.showMessage(LoginRegisterThirdActivity.this, response);
                try {
                    JSONObject obj;
                    obj = new JSONObject(response);
                    // 成功则Toast，并返回Login界面
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(LoginRegisterThirdActivity.this, "恭喜您,注册成功!");
                        Intent intent = new Intent(LoginRegisterThirdActivity.this, LoginActivity.class);
                        intent.putExtra("username_register", mEdtUsername.getText().toString());
                        intent.putExtra("password_register", mEdtPassword.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.showMessage(LoginRegisterThirdActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.showMessage(LoginRegisterThirdActivity.this, volleyError.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginRegisterThirdActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", mEdtUsername.getText().toString().trim());
                map.put("nickname", mEdtUsername.getText().toString().trim());
                map.put("name", mEdtName.getText().toString().trim());
                map.put("password", mEdtPassword.getText().toString().trim());
                map.put("mobile", mEdtPhone.getText().toString().trim());
                map.put("email", mEdtEmail.getText().toString().trim());
                map.put("accounttypekey", type);
                map.put("openid", openId);
                map.put("unionid", TextUtils.isEmpty(unionid) ? openId : unionid);
                map.put("accesstoken", accessToken);
                map.put("expirestimeout", expiresin);
                map.put("useravatarurl", userIcon);
                map.put("sex", userGender);
                map.put("deviceid", deviceid);
                map.put("qq", " ");
                return map;
            }
        };
        stringRequest.setTag(Tag_Volley);
        AppContext.getHttpQueue().add(stringRequest);
    }

    /**
     * 检查用户名是否可用
     */
    private void RequestVaildUser(final String username) {
        progressdialogshow(this);
        StringRequest requestThird = new StringRequest(Request.Method.POST, Constants.URL_Login_VAILD, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressdialogcancel();
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    ToastUtil.showMessage(LoginRegisterThirdActivity.this, obj.getString("message"));
//                    mBtnCheckUserName.setText(obj.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginRegisterThirdActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "username");
                map.put("value", username);
                return map;
            }
        };
        requestThird.setTag(Tag_Volley);
        AppContext.getHttpQueue().add(requestThird);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_registerinfo_back:
                startActivity(new Intent(LoginRegisterThirdActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btn_registerinfo_checkusername:
                String localusername = mEdtUsername.getText().toString();
                if (TextUtils.isEmpty(localusername)) {
                    ToastUtil.showMessage(this, "请输入用户名");
                    return;
                }
                RequestVaildUser(localusername);
                break;
            case R.id.btn_registerinfo_tonext:
                // 判断是否填入内容
                if (TextUtils.isEmpty(mEdtUsername.getText().toString())
                        || TextUtils.isEmpty(mEdtPassword.getText().toString())
                        || TextUtils.isEmpty(mEdtRepassword.getText().toString())
                        || TextUtils.isEmpty(mEdtName.getText().toString())
                        || TextUtils.isEmpty(mEdtEmail.getText().toString())
                        || TextUtils.isEmpty(mEdtPhone.getText().toString())) {
                    ToastUtil.showMessage(LoginRegisterThirdActivity.this, "请检查信息是否完整");
                    return;
                }
                // 判断密码一致
                if (!mEdtPassword.getText().toString().equals(mEdtRepassword.getText().toString())) {
                    ToastUtil.showMessage(LoginRegisterThirdActivity.this, "您设置的密码不一致");
                    return;
                }
                // 注册
                RequestToCommit();
                break;
        }
    }
}