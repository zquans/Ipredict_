package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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

/**
 * Created by Administrator on 2016/11/21.
 */
public class LoginResetActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtPassword, mEdtPasswordAgain;
    private Button mBtnSubmit;

    private String localtoken, local_phone_or_email;
    private String URL_RESET_PASSWORD = "http://api.iyuce.com/v1/account/reset_password";

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
        local_phone_or_email = getIntent().getStringExtra("local_phone_or_email");

        mEdtPassword = (EditText) findViewById(R.id.edt_activity_loginforget_password);
        mEdtPasswordAgain = (EditText) findViewById(R.id.edt_activity_loginforget_passwordagain);
        mBtnSubmit = (Button) findViewById(R.id.btn_activity_loginforget_submit);

        mBtnSubmit.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        StringRequest resetRequest = new StringRequest(Request.Method.POST, URL_RESET_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i("response = " + response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(LoginResetActivity.this, "恭喜您，密码重置成功啦");
                        startActivity(new Intent(LoginResetActivity.this, LoginActivity.class));
                        finish();
                    }else{
                        ToastUtil.showMessage(LoginResetActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(LoginResetActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", local_phone_or_email);
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
                if (!mEdtPassword.getText().toString().equals(mEdtPasswordAgain.getText().toString())) {
                    ToastUtil.showMessage(this, "两次输入的密码不一致哦");
                    break;
                }
                if (TextUtils.isEmpty(mEdtPassword.getText().toString().trim())
                        || TextUtils.isEmpty(mEdtPasswordAgain.getText().toString().trim())
                        ) {
                    ToastUtil.showMessage(this, "请检查内容是否填充完整");
                    break;
                }
                resetPassword();
                break;
        }
    }
}