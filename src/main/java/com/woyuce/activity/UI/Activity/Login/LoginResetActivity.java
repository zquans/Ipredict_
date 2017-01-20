package com.woyuce.activity.UI.Activity.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/21.
 */
public class LoginResetActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtPassword, mEdtPasswordAgain;
    private Button mBtnSubmit;

    private String localtoken, local_phone_or_email;

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_LOGIN_RESET);
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
        HttpHeaders headers = new HttpHeaders();
        localtoken = PreferenceUtil.getSharePre(LoginResetActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("key", local_phone_or_email);
        params.put("value", mEdtPassword.getText().toString().trim());
        OkGo.post(Constants.URL_POST_LOGIN_RESET_PASSWORD).tag(Constants.ACTIVITY_LOGIN_RESET).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            if (obj.getString("code").equals("0")) {
                ToastUtil.showMessage(LoginResetActivity.this, "恭喜您，密码重置成功啦");
                startActivity(new Intent(LoginResetActivity.this, LoginActivity.class));
                finish();
            } else {
                ToastUtil.showMessage(LoginResetActivity.this, obj.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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