package com.woyuce.activity.Controller.Store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/16
 */
public class StoreAddAddressActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLinearLayoutChoose;
    private TextView mChooseOne, mChooseTwo, mTxtPhone, mTxtEmail;
    private Button mBtnSendMsg, mBtnValidate;
    private EditText mEdtName, mEdtPhone, mEdtCode, mEdtQQ, mEdtEmail;

    private String local_name, local_mobile, local_qq, local_email, local_id, local_mobile_veri_code_id, local_verified_type;
    private String local_user_id, LocalMobileVeriCodeId;//LocalMobileVeriCodeId验证短信后返回的标识ID

    private String URL = "http://api.iyuce.com/v1/store/OperationAddress";
    //    private String URL_SEND_PHONE_MSG = "http://api.iyuce.com/v1/common/sendsmsvericode";
//    private String URL_SEND_EMAIL_MSG = "http://api.iyuce.com/v1/common/sendemailvericode";
//    private String URL_TO_VALIDATE = "http://api.iyuce.com/v1/common/verifycode";

    private int time_count;

    //创建一个Handler去处理倒计时事件
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mBtnSendMsg.setText(msg.obj.toString() + "秒后重发");
            mBtnSendMsg.setClickable(false);
            if ((int) msg.obj == 0) {
                mBtnSendMsg.setText("发送验证码");
                time_count = 61;
                mBtnSendMsg.setClickable(true);
            }
        }
    };

    public void back(View view) {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_ADD_ADDRESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_add_address);

        initView();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(this).getString("localtoken", "");
        local_user_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        Intent intent = getIntent();
        local_name = intent.getStringExtra("local_name");
        local_mobile = intent.getStringExtra("local_mobile");
        local_qq = intent.getStringExtra("local_qq");
        local_email = intent.getStringExtra("local_email");
        local_id = intent.getStringExtra("local_id");
        local_mobile_veri_code_id = intent.getStringExtra("local_mobile_veri_code_id");
        local_verified_type = intent.getStringExtra("local_verified_type");

        mLinearLayoutChoose = (LinearLayout) findViewById(R.id.ll_activity_storeaddaddress_choose);
        mChooseOne = (TextView) findViewById(R.id.txt_activity_storeaddaddress_choose_one);
        mChooseTwo = (TextView) findViewById(R.id.txt_activity_storeaddaddress_choose_two);
        mTxtPhone = (TextView) findViewById(R.id.txt_activity_storeaddaddress_phone);
        mTxtEmail = (TextView) findViewById(R.id.txt_activity_storeaddaddress_email);
        mChooseOne.setOnClickListener(this);
        mChooseTwo.setOnClickListener(this);
        mBtnSendMsg = (Button) findViewById(R.id.btn_activity_storeaddaddress_sendmsg);
        mBtnValidate = (Button) findViewById(R.id.btn_activity_storeaddaddress_validate);
        mEdtName = (EditText) findViewById(R.id.edt_activity_storeaddaddress_name);
        mEdtPhone = (EditText) findViewById(R.id.edt_activity_storeaddaddress_phone);
        mEdtCode = (EditText) findViewById(R.id.edt_activity_storeaddaddress_code);
        mEdtQQ = (EditText) findViewById(R.id.edt_activity_storeaddaddress_qq);
        mEdtEmail = (EditText) findViewById(R.id.edt_activity_storeaddaddress_email);

        if (!TextUtils.isEmpty(local_name)) {
            mEdtName.setText(local_name);
            mEdtPhone.setText(local_mobile);
            mEdtQQ.setText(local_qq);
            mEdtEmail.setText(local_email);
            mBtnSendMsg.setVisibility(View.GONE);
            mBtnValidate.setVisibility(View.GONE);
            mEdtCode.setVisibility(View.GONE);
            mLinearLayoutChoose.setVisibility(View.GONE);
            if (local_verified_type.equals("mobile")) {
                //如果之前是通过电话验证的，则修改地址不允许修改电话
                mEdtPhone.setEnabled(false);
            } else {
                //如果之前是通过邮箱验证的，则修改地址不允许修改邮箱
                mEdtEmail.setEnabled(false);
            }
        } else {
            mBtnSendMsg.setOnClickListener(this);
            mBtnValidate.setOnClickListener(this);
        }
    }

    /**
     * 操作地址请求
     */
    private void operaAddressRequest(String url) {
        HashMap<String, String> params = new HashMap<>();
        if (isPhoneNotEmail) {
            params.put("mobile", mEdtPhone.getText().toString());
            params.put("email", mEdtEmail.getText().toString());
            params.put("VerifiedType", "mobile");
        } else {
            params.put("mobile", mEdtEmail.getText().toString());
            params.put("email", mEdtPhone.getText().toString());
            params.put("VerifiedType", "email");
        }
        params.put("name", mEdtName.getText().toString());
        params.put("qq", mEdtQQ.getText().toString());
        params.put("MobileVeriCodeId", LocalMobileVeriCodeId);
        //修改的时候传id,新增的时候传0或者空
        if (TextUtils.isEmpty(local_id)) {
            params.put("id", "");
        } else {
            params.put("id", local_id);
        }
        HttpUtil.post(url, params, Constants.ACTIVITY_STORE_ADD_ADDRESS, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "修改地址成功");
                        StoreAddAddressActivity.this.finish();
                    } else {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "操作失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 操作地址(保存、修改、删除)
     */
    public void save(View view) {
//        if (TextUtils.isEmpty(mEdtCode.getText().toString())) {
//            ToastUtil.showMessage(StoreAddAddressActivity.this, "请输入验证码");
//            return;
//        }
//        requestValidate(URL_TO_VALIDATE);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("local_name"))) {
            isValidated = true;
        }
        if (!isValidated) {
            ToastUtil.showMessage(this, "请先确认验证码哦，亲");
            return;
        }
        if (TextUtils.isEmpty(local_id)) {
            operaAddressRequest(URL + "?operation=save&userid=" + local_user_id);
        } else {
            LocalMobileVeriCodeId = local_mobile_veri_code_id;
            operaAddressRequest(URL + "?operation=save&userid=" + local_user_id);
        }
    }

    /**
     * 发送验证码
     */
    private void RequestMsg(String url, final String key) {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put(key, mEdtPhone.getText().toString().trim());
        params.put("template", "VeriCode");
        HttpUtil.post(url, headers, params, Constants.ACTIVITY_STORE_ADD_ADDRESS, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                LogUtil.e("MsgRequest = " + result);
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        //倒计时
                        toCount();
                    } else {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, obj.getString("message"));
                    }
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 验证验证码
     */
    private void requestValidate(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("email_or_mobile", mEdtPhone.getText().toString().trim());
        params.put("code", mEdtCode.getText().toString().trim());
        HttpUtil.post(url, headers, params, Constants.ACTIVITY_STORE_ADD_ADDRESS, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                LogUtil.i("requestValidate = " + result);
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "验证成功啦,亲!");
                        LocalMobileVeriCodeId = obj.getString("data");
                        isValidated = true;
                    } else {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "验证失败，请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String localtoken;
    private boolean isValidated = false;
    private boolean isPhoneNotEmail = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_activity_storeaddaddress_choose_one:
                mChooseTwo.setTextColor(Color.parseColor("#f7941d"));
                mChooseTwo.setBackgroundColor(Color.parseColor("#fbfbfb"));
                mChooseOne.setTextColor(Color.parseColor("#fbfbfb"));
                mChooseOne.setBackgroundColor(Color.parseColor("#f7941d"));
                mEdtPhone.setHint("请输入手机号码");
                mEdtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                mEdtPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEdtPhone.setText("");
                mEdtEmail.setHint("请输入邮箱");
                mEdtEmail.setInputType(InputType.TYPE_CLASS_TEXT);
                mEdtEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
                mEdtEmail.setText("");
                mTxtEmail.setText("邮箱");
                mTxtPhone.setText("手机号");
                isPhoneNotEmail = true;
                break;
            case R.id.txt_activity_storeaddaddress_choose_two:
                mChooseOne.setTextColor(Color.parseColor("#f7941d"));
                mChooseOne.setBackgroundColor(Color.parseColor("#fbfbfb"));
                mChooseTwo.setTextColor(Color.parseColor("#fbfbfb"));
                mChooseTwo.setBackgroundColor(Color.parseColor("#f7941d"));
                mEdtEmail.setHint("请输入手机号码");
                mEdtEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                mEdtEmail.setInputType(InputType.TYPE_CLASS_NUMBER);
                mEdtEmail.setText("");
                mEdtPhone.setHint("请输入邮箱");
                mEdtPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                mEdtPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
                mEdtPhone.setText("");
                mTxtEmail.setText("手机号");
                mTxtPhone.setText("邮箱");
                isPhoneNotEmail = false;
                break;
            case R.id.btn_activity_storeaddaddress_sendmsg:
                if (TextUtils.isEmpty(mEdtPhone.getText().toString())) {
                    if (isPhoneNotEmail)
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "手机号不能为空哦");
                    else
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "邮箱不能为空哦");
                    return;
                }
                if (isPhoneNotEmail)
                    RequestMsg(Constants.URL_POST_LOGIN_SEND_PHONE_MSG, "phone");
                else
                    RequestMsg(Constants.URL_POST_LOGIN_SEND_EMAIL_MSG, "email");
//                requestPhoneValidate(URL_SEND_MSG + "?phone=" + mEdtPhone.getText().toString() + "&userid=" + local_user_id);
                break;
            case R.id.btn_activity_storeaddaddress_validate:
                if (TextUtils.isEmpty(mEdtCode.getText().toString())) {
                    ToastUtil.showMessage(StoreAddAddressActivity.this, "请输入验证码");
                    return;
                }
                requestValidate(Constants.URL_POST_LOGIN_VERIFY_CODE);
//                requestPhoneValidate(URL_VALIDATE + "?phone=" + mEdtPhone.getText().toString() + "&code=" + mEdtCode.getText().toString());
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