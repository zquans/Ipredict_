package com.woyuce.activity.UI.Activity.Login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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

/**
 * Created by Administrator on 2017/3/27
 */
public class LoginBindNew extends BaseActivity implements View.OnClickListener {

    private EditText mEdtUser, mEdtPassword;
    private Button mBtnCommit;

    private String type, openId, unionid, accessToken, expiresin, localtoken;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getHttpQueue().cancelAll("bindRequest");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third);

        initView();
    }

    private void initView() {
        type = getIntent().getStringExtra("type");
        openId = getIntent().getStringExtra("openId");
        unionid = getIntent().getStringExtra("unionid");
        accessToken = getIntent().getStringExtra("accessToken");
        expiresin = getIntent().getStringExtra("expiresin");
        mEdtUser = (EditText) findViewById(R.id.edt_activity_login_third_user);
        mEdtPassword = (EditText) findViewById(R.id.edt_activity_login_third_password);
        mEdtPassword.setText(unionid);
        mBtnCommit = (Button) findViewById(R.id.btn_activity_login_third_submit);
        mBtnCommit.setOnClickListener(this);
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
        }
    }

    private void requestBind(final String user, final String password) {
        StringRequest bindRequest = new StringRequest(Request.Method.POST, Constants.URL_Login_To_Bind, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ToastUtil.showMessage(LoginBindNew.this, "s = " + s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(LoginBindNew.this, "绑定成功");
                        LoginBindNew.this.finish();
                    } else {
                        ToastUtil.showMessage(LoginBindNew.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginBindNew.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("accounttypekey", type);
                map.put("openid", openId);
                map.put("unionid", unionid);
                map.put("accesstoken", accessToken);
                map.put("expiresin", expiresin + "");
                map.put("username", user);
                map.put("password", password);
                map.put("deviceid", AppContext.getDeviceToken());
                return map;
            }
        };
        bindRequest.setTag("bindRequest");
        AppContext.getHttpQueue().add(bindRequest);
    }
}