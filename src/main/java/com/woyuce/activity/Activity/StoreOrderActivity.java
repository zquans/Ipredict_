package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/21.
 */
public class StoreOrderActivity extends BaseActivity {

    private EditText mEdtOrder, mEdtMoney;

    //生成订单
    private String URL_TO_ORDER = "http://api.iyuce.com/v1/store/order";

    private String total_price, local_address_id, local_skuids, local_store_user_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeorder);

        initView();
    }

    private void initView() {
        total_price = getIntent().getStringExtra("total_price");
        local_address_id = getIntent().getStringExtra("address");
        local_skuids = getIntent().getStringExtra("skuids");
        local_store_user_money = getIntent().getStringExtra("discount");

        mEdtOrder = (EditText) findViewById(R.id.edt_activity_storeorder_order);
        mEdtMoney = (EditText) findViewById(R.id.edt_activity_storeorder_money);

        mEdtMoney.setText(total_price);
        //生成订单请求
        requestOrder();
    }

    /**
     * 生成订单
     */
    private void requestOrder() {
        StringRequest addressRequest = new StringRequest(Request.Method.POST, URL_TO_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("s = " + s);
                JSONObject obj;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        mEdtOrder.setText(obj.getString("message"));
                    } else {
                        LogUtil.i("生成订单错误" + obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("discount", local_store_user_money);
                map.put("address", local_address_id);
                map.put("userid", PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("userId", ""));
                map.put("skuids", local_skuids);
                LogUtil.i("aaaa = " + local_store_user_money + "," + local_address_id + "," + local_skuids + ","
                        + PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("userId", ""));
                return map;
            }
        };
        addressRequest.setTag("addressRequest");
        AppContext.getHttpQueue().add(addressRequest);
    }

    public void toPay(View view) {
        ToastUtil.showMessage(this, "去调支付宝吧");
    }
}