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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
public class StoreAddAddressActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnSendMsg, mBtnValidate;
    private EditText mEdtName, mEdtPhone, mEdtCode, mEdtQQ, mEdtEmail;

    private String local_name, local_mobile, local_qq, local_email, local_id, local_mobile_veri_code_id;
    private String local_user_id, LocalMobileVeriCodeId;//LocalMobileVeriCodeId验证短信后返回的标识ID

    private String URL = "http://api.iyuce.com/v1/store/OperationAddress";
    private String URL_SEND_MSG = "http://api.iyuce.com/v1/store/sendsmscode";
    private String URL_VALIDATE = "http://api.iyuce.com/v1/store/validsmscode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeaddaddress);

        initView();
    }

    private void initView() {
        local_user_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        Intent intent = getIntent();
        local_name = intent.getStringExtra("local_name");
        local_mobile = intent.getStringExtra("local_mobile");
        local_qq = intent.getStringExtra("local_qq");
        local_email = intent.getStringExtra("local_email");
        local_id = intent.getStringExtra("local_id");
        local_mobile_veri_code_id = intent.getStringExtra("local_mobile_veri_code_id");

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
        } else {
            mBtnSendMsg.setOnClickListener(this);
            mBtnValidate.setOnClickListener(this);
        }
    }

    private void operaAddressRequest(String url) {
        StringRequest addressListRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("s = " + s);
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "成功 = " + obj.getString("message"));
                        LogUtil.i("成功" + obj.toString());
                    } else {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, "操作失败：" + obj.getString("message"));
                        LogUtil.i("失败" + obj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", mEdtName.getText().toString());
                map.put("mobile", mEdtPhone.getText().toString());
                map.put("qq", mEdtQQ.getText().toString());
                map.put("email", mEdtEmail.getText().toString());
                //TODO 修改的时候传id,新增的时候传0或者空
                map.put("id", local_id);
                map.put("MobileVeriCodeId", LocalMobileVeriCodeId);
                LogUtil.i(mEdtName.getText().toString() + mEdtPhone.getText().toString() +
                        mEdtQQ.getText().toString() + mEdtEmail.getText().toString() +
                        LocalMobileVeriCodeId);
                return map;
            }
        };
        addressListRequest.setTag("storeAddAddressRequest");
        AppContext.getHttpQueue().add(addressListRequest);
    }

    /**
     * 发送手机验证码及验证手机验证码的请求
     */
    private void requestPhoneValidate(final String url) {
        StringRequest validateRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        if (url.contains(URL_SEND_MSG)) {
                            ToastUtil.showMessage(StoreAddAddressActivity.this, "验证码发送成功");
                        } else {
                            ToastUtil.showMessage(StoreAddAddressActivity.this, "验证成功");
                            LocalMobileVeriCodeId = obj.getString("data");
                            LogUtil.i("LocalMobileVeriCodeId = " + obj + "," + LocalMobileVeriCodeId);
                        }
                    } else {
                        ToastUtil.showMessage(StoreAddAddressActivity.this, obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        validateRequest.setTag("storeAddAddressRequest");
        AppContext.getHttpQueue().add(validateRequest);
    }

    /**
     * 操作地址(保存、修改、删除)
     *
     * @param view
     */
    public void save(View view) {
        if (TextUtils.isEmpty(local_id)) {
            LogUtil.e("URL= " + URL + "?operation=save&userid=" + local_user_id);
            operaAddressRequest(URL + "?operation=save&userid=" + local_user_id);
        } else {
            LocalMobileVeriCodeId = local_mobile_veri_code_id;
            LogUtil.e("URL= " + URL + "?operation=save&id=" + local_id + "&userid=" + local_user_id + ",,," + LocalMobileVeriCodeId);
            operaAddressRequest(URL + "?operation=save" + "&userid=" + local_user_id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_storeaddaddress_sendmsg:
                requestPhoneValidate(URL_SEND_MSG + "?phone=" + mEdtPhone.getText().toString() + "&userid=" + local_user_id);
                break;
            case R.id.btn_activity_storeaddaddress_validate:
                requestPhoneValidate(URL_VALIDATE + "?phone=" + mEdtPhone.getText().toString() + "&code=" + mEdtCode.getText().toString());
                break;
        }
    }
}