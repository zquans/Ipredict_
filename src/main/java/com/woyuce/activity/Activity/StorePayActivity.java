package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StorePayActivity extends BaseActivity {

    private Button mBtnAddress;

    //获取默认收货地址
    private String URL = "http://api.iyuce.com/v1/store/getdefaultaddr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storepay);

        initView();
    }

    private void initView() {
        TextView mTxtPrice = (TextView) this.findViewById(R.id.txt_storecar_final_price);
        mTxtPrice.setText(getIntent().getDoubleExtra("goods_price", -1.00) + "元");

        mBtnAddress = (Button) findViewById(R.id.btn_activity_store_pay);
        requestAddress();
    }

    private void requestAddress() {
        URL = URL + "?userid=" + PreferenceUtil.getSharePre(this).getString("userId", "");
        StringRequest addressRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(s);
                    arr = obj.getJSONArray("address");
                    if (arr.length() == 0) {
                        mBtnAddress.setText("获取到的默认收货地址");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        addressRequest.setTag("addressRequest");
        AppContext.getHttpQueue().add(addressRequest);
    }


    public void addAddress(View view) {
        startActivity(new Intent(this, StoreAddressActivity.class));
    }

    public void nowPay(View view) {
        ToastUtil.showMessage(this, "去调支付吧,你要付这么多钱" + getIntent().getDoubleExtra("goods_price", -1.00) + "元");
    }
}