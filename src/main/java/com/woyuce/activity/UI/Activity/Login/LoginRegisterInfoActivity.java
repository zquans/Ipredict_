package com.woyuce.activity.UI.Activity.Login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
 */
public class LoginRegisterInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtNickname, edtPassword, edtRepassword, edtUsername, edtEmailOrPhone, edtTime, edtCity, edtInvitenum;
    private TextView txtback, txtPhoneOrEmail;
    private Button btnfinish, btnCheckUsername, btnCheckEmail;

    private String localtoken, localPhoneOrEmail, email_or_phone, localtimer;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginRegisterInfoActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_LOGIN_REGISTER_INFO);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregisterinfo);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        email_or_phone = intent.getStringExtra("email_or_phone");
        localPhoneOrEmail = intent.getStringExtra("local_phone_or_email");

        txtback = (TextView) findViewById(R.id.txt_registerinfo_back);
        txtPhoneOrEmail = (TextView) findViewById(R.id.txt_registerinfo_email_or_phone);
        edtNickname = (EditText) findViewById(R.id.edt_registerinfo_nickname);
        edtPassword = (EditText) findViewById(R.id.edt_registerinfo_password);
        edtRepassword = (EditText) findViewById(R.id.edt_registerinfo_repassword);
        edtUsername = (EditText) findViewById(R.id.edt_registerinfo_username);
        edtEmailOrPhone = (EditText) findViewById(R.id.edt_registerinfo_email_or_phone);
        edtCity = (EditText) findViewById(R.id.edt_registerinfo_city);
        edtTime = (EditText) findViewById(R.id.edt_registerinfo_time);
        edtInvitenum = (EditText) findViewById(R.id.edt_registerinfo_invitednum);
        btnfinish = (Button) findViewById(R.id.btn_registerinfo_tonext);
        btnCheckUsername = (Button) findViewById(R.id.btn_registerinfo_checkusername);
        btnCheckEmail = (Button) findViewById(R.id.btn_registerinfo_checkemail);

        txtback.setOnClickListener(this);
        edtTime.setOnClickListener(this);
        btnfinish.setOnClickListener(this);
        btnCheckUsername.setOnClickListener(this);
        // btnCheckEmail.setOnClickListener(this);

        if (email_or_phone.equals("phone")) {
            txtPhoneOrEmail.setText("电子邮箱");
            edtEmailOrPhone.setHint("请输入您的电子邮箱");
        } else {
            txtPhoneOrEmail.setText("手机号码");
            edtEmailOrPhone.setHint("请输入您的手机号码");
        }
        if (email_or_phone.equals("phone")) {
            edtEmailOrPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    RequestVaild("email", s.toString());
                }
            });
        } else {
            edtEmailOrPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            edtEmailOrPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtEmailOrPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() >= 11) {
                        RequestVaild("mobile", s.toString());
                    }
                }
            });
        }
    }

    private void RequestToNext() {
        HttpHeaders headers = new HttpHeaders();
        localtoken = PreferenceUtil.getSharePre(LoginRegisterInfoActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        if (email_or_phone.equals("phone")) {
            params.put("mobile", localPhoneOrEmail);
            params.put("email", edtEmailOrPhone.getText().toString().trim());
        } else {
            params.put("mobile", edtEmailOrPhone.getText().toString().trim());
            params.put("email", localPhoneOrEmail);
        }
        params.put("username", edtUsername.getText().toString().trim());
        params.put("nickname", edtNickname.getText().toString().trim());
        params.put("password", edtPassword.getText().toString().trim());
        params.put("name", "");
        params.put("avatar", "");
        params.put("sex", "");
        params.put("province", "");
        params.put("city", "");
        params.put("aliwangwang", "");
        params.put("qq", "");
        params.put("passwordanswer", "");
        params.put("passwordquestion", "");
        params.put("examtime", "");
        params.put("invite", "");
        OkGo.post(Constants.URL_POST_LOGIN_REGISTER).tag(Constants.ACTIVITY_LOGIN_REGISTER_INFO).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doRegisterSuccess(s);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterInfoActivity.this, "网络错误，请稍候重试");
                    }
                });
    }

    private void doRegisterSuccess(String response) {
        JSONObject obj;
        try {
            // String parseString = new String(response.getBytes("ISO-8859-1"), "utf-8");
            obj = new JSONObject(response);
            // 成功则Toast，并返回Login界面
            if (obj.getString("code").equals("0")) {
                ToastUtil.showMessage(LoginRegisterInfoActivity.this, "恭喜您,注册成功!");
                Intent intent = new Intent(LoginRegisterInfoActivity.this, LoginActivity.class);
                intent.putExtra("username_register", edtUsername.getText().toString());
                intent.putExtra("password_register", edtPassword.getText().toString());
                intent.putExtra("timer_register", localtimer);
                startActivity(intent);
                finish();
            } else {
                ToastUtil.showMessage(LoginRegisterInfoActivity.this, obj.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查邮箱是否可用
     */
    private void RequestVaild(final String key, final String value) {
        HttpHeaders headers = new HttpHeaders();
        localtoken = PreferenceUtil.getSharePre(LoginRegisterInfoActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("key", key);
        params.put("value", value);
        OkGo.post(Constants.URL_POST_LOGIN_VAILD).tag(Constants.ACTIVITY_LOGIN_REGISTER_INFO).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        JSONObject obj;
                        try {
                            obj = new JSONObject(s);
                            int result = obj.getInt("data");
                            if (result == 0) {
                                ToastUtil.showMessage(LoginRegisterInfoActivity.this, obj.getString("message"));
                            } else {
                                ToastUtil.showMessage(LoginRegisterInfoActivity.this, obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterInfoActivity.this, "发送失败，请稍候再试");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_registerinfo_back:
                startActivity(new Intent(LoginRegisterInfoActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.edt_registerinfo_time:
                new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog, DateSet, 2018, 07, 8).show();
                break;
            case R.id.btn_registerinfo_checkusername:
                String localusername = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(localusername)) {
                    ToastUtil.showMessage(LoginRegisterInfoActivity.this, "请输入用户名");
                    break;
                }
                RequestVaild("username", localusername);
                break;
            case R.id.btn_registerinfo_checkemail:
                String localemail = edtEmailOrPhone.getText().toString().trim();
                if (TextUtils.isEmpty(localemail)) {
                    if (email_or_phone.equals("phone")) {
                        ToastUtil.showMessage(LoginRegisterInfoActivity.this, "请输入电子邮箱");
                    } else {
                        ToastUtil.showMessage(LoginRegisterInfoActivity.this, "请输入手机号码");
                    }
                    break;
                }
                RequestVaild("email", localemail);
                break;
            case R.id.btn_registerinfo_tonext:
                LogUtil.e("alledt = " + edtNickname.getText() + edtUsername.getText() + edtPassword.getText()
                        + edtRepassword.getText() + edtEmailOrPhone.getText() + localPhoneOrEmail + localtoken);
                // 判断是否填入内容
                if (TextUtils.isEmpty(edtNickname.getText().toString().trim())
                        || TextUtils.isEmpty(edtUsername.getText().toString().trim())
                        || TextUtils.isEmpty(edtPassword.getText().toString().trim())
                        || TextUtils.isEmpty(edtRepassword.getText().toString().trim())
                        || TextUtils.isEmpty(edtEmailOrPhone.getText().toString().trim())) {
                    ToastUtil.showMessage(LoginRegisterInfoActivity.this, "请检查信息是否完整");
                    return;
                }
                // 判断密码一致
                if (!edtPassword.getText().toString().equals(edtRepassword.getText().toString())) {
                    ToastUtil.showMessage(LoginRegisterInfoActivity.this, "您设置的密码不一致");
                    return;
                }
                // 注册
                RequestToNext();
                break;
        }
    }

    // 时间选择器
    DatePickerDialog.OnDateSetListener DateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            edtTime.setText(
                    new StringBuffer().append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth));
            // 将此处的时间设置进数据
            PreferenceUtil.save(LoginRegisterInfoActivity.this, "mtimer", year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            localtimer = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        }
    };
}