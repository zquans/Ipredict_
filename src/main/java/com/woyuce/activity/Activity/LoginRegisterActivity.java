package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
public class LoginRegisterActivity  extends BaseActivity implements View.OnClickListener {

    private TextView txtback;
    private EditText mEdtPhonenum, mEdtAcceptMsg;
    private Button btnSend, btnToNext;
    private String localtoken, localChecknum;

    private String URL_SENDMSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
    private String URL_TONEXT = "http://api.iyuce.com/v1/common/verifycode";
    private String URL_VAILD = "http://api.iyuce.com/v1/account/valid";

    private int time_count;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginRegisterActivity.this,LoginActivity.class));
        finish();
    }

    //创建一个Handler去处理倒计时事件
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            btnSend.setText(msg.obj.toString() + "秒后重发");
            btnSend.setClickable(false);
            if((int)msg.obj == 0){
                btnSend.setText("发送验证码");
                time_count = 61;
                btnSend.setClickable(true);
            }
        };
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
        txtback = (TextView) findViewById(R.id.txt_register_back);
        mEdtPhonenum = (EditText) findViewById(R.id.edit_register_acceptphonenum);
        mEdtAcceptMsg = (EditText) findViewById(R.id.edit_register_acceptmsg);
        btnSend = (Button) findViewById(R.id.btn_register_sendmsg);
        btnToNext = (Button) findViewById(R.id.btn_register_tonext);

        txtback.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnToNext.setOnClickListener(this);
    }

    /**
     * 检查电话是否可用
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
                            RequestMsg();
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
                map.put("key", "mobile");
                map.put("value", mEdtPhonenum.getText().toString().trim());
                return map;
            }
        };
        VaildRequest.setTag("register");
        AppContext.getHttpQueue().add(VaildRequest);
    }

    /**
     * 发送短信
     */
    private void RequestMsg() {
        StringRequest MsgRequest = new StringRequest(Request.Method.POST, URL_SENDMSG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("response 2 = " + response);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "已发送，请注意短信哦,亲!");
                        //倒计时
                        toCount();
                    } else {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "发送失败，请重试");
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
                map.put("phone", mEdtPhonenum.getText().toString().trim());
                map.put("template", "VeriCode");
                return map;
            }
        };
        MsgRequest.setTag("register");
        AppContext.getHttpQueue().add(MsgRequest);
    }

    // 验证验证码
    private void requeToNext() {
        StringRequest CheckRequest = new StringRequest(Request.Method.POST, URL_TONEXT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        ToastUtil.showMessage(LoginRegisterActivity.this, "验证成功啦,亲!");
                        Intent intent = new Intent(LoginRegisterActivity.this, LoginRegisterInfoActivity.class);
                        intent.putExtra("localPhonenum", mEdtPhonenum.getText().toString().trim());
                        startActivity(intent);
                        finish();
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("email_or_mobile", mEdtPhonenum.getText().toString().trim());
                map.put("code", localChecknum);
                return map;
            }
        };
        CheckRequest.setTag("register");
        AppContext.getHttpQueue().add(CheckRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_register_back:
                startActivity(new Intent(LoginRegisterActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.btn_register_sendmsg:
                if (TextUtils.isEmpty(mEdtPhonenum.getText().toString().trim())) {
                    ToastUtil.showMessage(LoginRegisterActivity.this, "请输入手机号码");
                    break;
                }
                RequestVaild();
                break;
            case R.id.btn_register_tonext:
                localChecknum = mEdtAcceptMsg.getText().toString().trim();
                requeToNext();
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
                if(time_count > 0 && time_count <61){
                    time_count = time_count - 1;
                    Message msg = Message.obtain();
                    msg.obj = time_count;
                    mHandler.sendMessage(msg);
                }else{
                    mTimer.cancel();
                }
            }
        }, 500,1000);
    }
}