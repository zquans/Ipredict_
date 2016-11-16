package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
public class StoreAddAddressActivity extends BaseActivity {

    private String URL = "http://api.iyuce.com/v1/store/OperationAddress?operation={operation}&id={id}&userid={userid}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeaddaddress);
    }

    //TODO 发送手机验证码

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

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", "李四");
                map.put("mobile", "15860906905");
                map.put("q_q", "2859382257");
                map.put("email", "2859382257@qq.com");
                map.put("id", "2859382257@qq.com");
                return map;
            }
        };
        addressListRequest.setTag("addressListRequest");
        AppContext.getHttpQueue().add(addressListRequest);
    }

    public void save(View view) {
        URL = URL + "?operation=save&userid=" + PreferenceUtil.getSharePre(this).getString("userId", "");
        operaAddressRequest(URL);
    }
}