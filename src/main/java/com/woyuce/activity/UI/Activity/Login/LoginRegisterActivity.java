package com.woyuce.activity.UI.Activity.Login;

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

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

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
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_LOGIN_REGISTER);

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
        HttpHeaders headers = new HttpHeaders();
        localtoken = PreferenceUtil.getSharePre(LoginRegisterActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        if (email_or_phone.equals("phone")) {
            params.put("key", "mobile");
        } else {
            params.put("key", "email");
        }
        params.put("value", mEdtPhonenum.getText().toString().trim());
        OkGo.post(Constants.URL_POST_LOGIN_VAILD).tag(Constants.ACTIVITY_LOGIN_REGISTER).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doValidateSuccess(s);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterActivity.this, "网络错误，请稍候再试");
                    }
                });
    }

    private void doValidateSuccess(String response) {
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            int result = obj.getInt("code");
            if (result == 0) {
                String localdata = obj.getString("data");
                if (localdata.equals("1")) {
                    if (email_or_phone.equals("phone")) {
                        RequestMsg(Constants.URL_POST_LOGIN_SEND_PHONE_MSG);
                    } else {
                        RequestMsg(Constants.URL_POST_LOGIN_SEND_EMAIL_MSG);
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

    /**
     * 发送验证码
     */
    private void RequestMsg(String url) {
        isVarify = true;

        HttpHeaders headers = new HttpHeaders();
        localtoken = PreferenceUtil.getSharePre(LoginRegisterActivity.this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(activity_email)) {
            params.put("email", mEdtPhonenum.getText().toString().trim());
        } else {
            if (email_or_phone.equals("phone")) {
                params.put("phone", mEdtPhonenum.getText().toString().trim());
            } else {
                params.put("email", mEdtPhonenum.getText().toString().trim());
            }
        }
        params.put("template", "VeriCode");
        OkGo.post(url).tag(Constants.ACTIVITY_LOGIN_REGISTER).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doRequestMsgSuccess(s);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterActivity.this, "网络错误，请稍候再试");
                    }
                });
    }

    private void doRequestMsgSuccess(String response) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证验证码
     */
    private void requeToNext(final String arg) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("email_or_mobile", mEdtPhonenum.getText().toString().trim());
        params.put("code", localChecknum);
        OkGo.post(Constants.URL_POST_LOGIN_VERIFY_CODE).tag(Constants.ACTIVITY_LOGIN_REGISTER).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doVerifyCodeSuccess(s, arg);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterActivity.this, "验证失败，请重试");
                    }
                });
    }

    private void doVerifyCodeSuccess(String response, String arg) {
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

    /**
     * 激活邮箱
     */
    private void requeActivityEmail(final String key, final String value) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("key", key);
        params.put("value", value);
        OkGo.post(Constants.URL_POST_LOGIN_ACTIVITE_EMAIL).tag(Constants.ACTIVITY_LOGIN_REGISTER).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doActiviteEmailSuccess(s);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showMessage(LoginRegisterActivity.this, "验证失败，请重试");
                    }
                });
    }

    private void doActiviteEmailSuccess(String response) {
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
                    RequestMsg(Constants.URL_POST_LOGIN_SEND_EMAIL_MSG);
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
                        RequestMsg(Constants.URL_POST_LOGIN_SEND_PHONE_MSG);
                    } else {
                        RequestMsg(Constants.URL_POST_LOGIN_SEND_EMAIL_MSG);
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