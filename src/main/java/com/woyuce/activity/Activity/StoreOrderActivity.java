package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StorePayResult;
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
    private String URL_TO_PAY = "http://api.iyuce.com/v1/store/pay";

    private String total_price, local_address_id, local_skuids, local_store_user_money;
    private String local_order_id;
    private String local_order_back_info;

    private static final int SDK_PAY_FLAG = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    StorePayResult result = new StorePayResult((Map<String, String>) msg.obj);
                    String resultInfo = result.getResult();// 同步返回需要验证的信息
                    String resultStatus = result.getResultStatus();
                    String resultMemo = result.getMemo();
                    LogUtil.i("result = " + ",resultInfo = " + resultInfo
                            + ",resultStatus = " + resultStatus + ",resultMemo = " + resultMemo);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showMessage(StoreOrderActivity.this, "支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showMessage(StoreOrderActivity.this, "支付成功");
                    }
                    break;
            }
        }
    };

    public void back(View view) {
        finish();
    }

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
                        local_order_id = obj.getString("message");
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
        addressRequest.setTag("StoreOrderActivity");
        AppContext.getHttpQueue().add(addressRequest);
    }

    private String URL_ALIPAY = "http://pay.iyuce.com/api/order/ApplyApp";

    public void toPay(View view) {
        ToastUtil.showMessage(this, "去调支付宝吧");
        StringRequest aliRequest = new StringRequest(Request.Method.POST, URL_ALIPAY, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    local_order_back_info = obj.getString("data");

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(StoreOrderActivity.this);
                            Map<String, String> result = alipay.payV2(local_order_back_info, true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.i("local_order_back_info = " + local_order_back_info);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError);
            }
        });
        aliRequest.setTag("StoreOrderActivity");
        AppContext.getHttpQueue().add(aliRequest);
    }

    public void toPay() {
        ToastUtil.showMessage(this, "去调支付宝吧");

        StringRequest payRequest = new StringRequest(Request.Method.POST, URL_TO_PAY + "?id="
                + local_order_id + "&userid=" + PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("userId", ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtil.i("s = " + s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError);
            }
        });
        payRequest.setTag("StoreOrderActivity");
        AppContext.getHttpQueue().add(payRequest);
    }
}