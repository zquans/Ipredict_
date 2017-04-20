package com.woyuce.activity.Controller.Login.Third;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.woyuce.activity.AppContext;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/27
 */
public class LoginBindNew extends BaseActivity implements View.OnClickListener {

    private EditText mEdtUser, mEdtPassword;
    private ImageButton mImgBtnClose;
    private Button mBtnCommit;

    private String type, openId, unionid, accessToken, expiresin, localtoken;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppContext.getHttpQueue().cancelAll("bindRequest");
        HttpUtil.removeTag(Constants.ACTIVITY_LOGIN_BIND_NEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third);

        initView();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(LoginBindNew.this).getString("localtoken", "");
        type = getIntent().getStringExtra("type");
        openId = getIntent().getStringExtra("openId");
        unionid = getIntent().getStringExtra("unionid");
        accessToken = getIntent().getStringExtra("accessToken");
        expiresin = getIntent().getStringExtra("expiresin");
        mEdtUser = (EditText) findViewById(R.id.edt_activity_login_third_user);
        mEdtPassword = (EditText) findViewById(R.id.edt_activity_login_third_password);
        mImgBtnClose = (ImageButton) findViewById(R.id.imgbtn_activity_loginforget_close);
        mBtnCommit = (Button) findViewById(R.id.btn_activity_login_third_submit);
        mBtnCommit.setOnClickListener(this);
        mImgBtnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_login_third_submit:
                if (mEdtUser.getText() == null) {
                    ToastUtil.showMessage(this, "账号不能为空");
                    return;
                }
                if (mEdtPassword.getText() == null) {
                    ToastUtil.showMessage(this, "密码不能为空");
                    return;
                }
                requestBind(mEdtUser.getText().toString(), mEdtPassword.getText().toString());
                break;
            case R.id.imgbtn_activity_loginforget_close:
                finish();
                break;
        }
    }

    private void requestBind(final String user, final String password) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("accounttypekey", type);
        params.put("openid", openId);
        params.put("unionid", unionid);
        params.put("unionid", TextUtils.isEmpty(unionid) ? openId : unionid);
        params.put("accesstoken", accessToken);
        params.put("expiresin", expiresin + "");
        params.put("username", user);
        params.put("password", password);
        params.put("deviceid", AppContext.getDeviceToken());
        HttpUtil.post(Constants.URL_Login_To_Bind, headers, params, Constants.ACTIVITY_LOGIN, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(LoginBindNew.this, "绑定成功");
                        obj = new JSONObject(obj.getString("data"));
                        PreferenceUtil.save(LoginBindNew.this, "userId", obj.getString("userid"));
                        PreferenceUtil.save(LoginBindNew.this, "mUserName", obj.getString("username"));
                        PreferenceUtil.save(LoginBindNew.this, "Permission", obj.getString("permission"));
                        PreferenceUtil.save(LoginBindNew.this, "money", obj.getString("tradepoints"));
                        PreferenceUtil.save(LoginBindNew.this, "update", obj.getString("login_time"));
                        PreferenceUtil.save(LoginBindNew.this, "mtimer", obj.getString("exam_time"));
                        startActivity(new Intent(LoginBindNew.this, MainActivity.class));
                        LoginBindNew.this.finish();
                    } else {
                        ToastUtil.showMessage(LoginBindNew.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}