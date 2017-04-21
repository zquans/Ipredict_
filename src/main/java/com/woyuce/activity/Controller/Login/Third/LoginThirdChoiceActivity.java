package com.woyuce.activity.Controller.Login.Third;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class LoginThirdChoiceActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImgBack, mImgType;
    private TextView mTxtTitle, mTxtTypeHint, mTxtJumpToLogin;
    private Button mBtnQuickRegister, mBtnToBind;

    private String localtoken, type, openId, unionid, accessToken, expiresin, userIcon, userGender, userName, deviceid;

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_LOGIN_REGISTER_THIRD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third_link);

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

        mImgBack = (ImageView) findViewById(R.id.img_header_back);
        mImgType = (ImageView) findViewById(R.id.img_link_type);
        mTxtTitle = (TextView) findViewById(R.id.txt_header_title);
        mTxtTypeHint = (TextView) findViewById(R.id.txt_login_third_link_platform);
        mTxtJumpToLogin = (TextView) findViewById(R.id.txt_login_third_link_login_right_now);
        mBtnQuickRegister = (Button) findViewById(R.id.btn_login_third_link_quick_register);
        mBtnToBind = (Button) findViewById(R.id.btn_login_third_link_to_link);

        mImgBack.setOnClickListener(this);
        mTxtJumpToLogin.setOnClickListener(this);
        mBtnQuickRegister.setOnClickListener(this);
        mBtnToBind.setOnClickListener(this);
        if (type.contains("qq") || type.contains("QQ")) {
            mTxtTitle.setText("QQ登陆");
            mTxtTypeHint.setText(getString(R.string.login_third_type, "QQ"));
            mImgType.setBackgroundResource(R.mipmap.icon_qq);
        } else {
            mTxtTitle.setText("微信登陆");
            mTxtTypeHint.setText(getString(R.string.login_third_type, "微信"));
            mImgType.setBackgroundResource(R.mipmap.icon_wechat);
        }
    }

    /**
     * 请求跳过第三方注册直接登录
     */
    private void jumpThird() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        localtoken = PreferenceUtil.getSharePre(LoginThirdChoiceActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("accounttypekey", type);
        params.put("openid", openId);
        params.put("unionid", TextUtils.isEmpty(unionid) ? openId : unionid);
        params.put("accesstoken", accessToken);
        params.put("expiresin", expiresin);
        params.put("nickname", userName);
        params.put("gender", userGender);
        params.put("useravatarurl", userIcon);
        params.put("deviceid", deviceid);
        HttpUtil.post(Constants.URL_Login_To_Jump, headers, params, Constants.ACTIVITY_LOGIN_REGISTER_THIRD, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                progressdialogcancel();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        //不是第一次登陆，直接进入下一个界面
                        obj = new JSONObject(obj.getString("data"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "userId", obj.getString("userid"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "mUserName", obj.getString("username"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "Permission", obj.getString("permission"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "money", obj.getString("tradepoints"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "update", obj.getString("login_time"));
                        PreferenceUtil.save(LoginThirdChoiceActivity.this, "mtimer", obj.getString("exam_time"));
                        startActivity(new Intent(LoginThirdChoiceActivity.this, MainActivity.class));
                        LoginThirdChoiceActivity.this.finish();
                    } else {
                        ToastUtil.showMessage(LoginThirdChoiceActivity.this, "网络错误，直接登录失败，请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_third_link_quick_register:
                Intent intent_to_register = new Intent(LoginThirdChoiceActivity.this, LoginRegisterThirdActivity.class);
                intent_to_register.putExtra("type", type);
                intent_to_register.putExtra("openId", openId);
                intent_to_register.putExtra("unionid", unionid);
                intent_to_register.putExtra("accessToken", accessToken);
                intent_to_register.putExtra("expiresin", expiresin);
                intent_to_register.putExtra("userIcon", userIcon);
                intent_to_register.putExtra("userGender", userGender);
                intent_to_register.putExtra("userName", userName);
                intent_to_register.putExtra("deviceid", deviceid);
                LoginThirdChoiceActivity.this.startActivity(intent_to_register);
                break;
            case R.id.btn_login_third_link_to_link:
                Intent intent_to_link = new Intent(LoginThirdChoiceActivity.this, LoginBindNew.class);
                intent_to_link.putExtra("type", type);
                intent_to_link.putExtra("openId", openId);
                intent_to_link.putExtra("unionid", unionid);
                intent_to_link.putExtra("accessToken", accessToken);
                intent_to_link.putExtra("expiresin", expiresin);
                LoginThirdChoiceActivity.this.startActivity(intent_to_link);
                break;
            case R.id.txt_login_third_link_login_right_now:
                jumpThird();
                break;
            case R.id.img_header_back:
                finish();
                break;
        }
    }
}