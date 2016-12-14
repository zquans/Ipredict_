package com.woyuce.activity.Act;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StorePayResult;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.wxapi.WXPayEntryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/21.
 */
public class StoreOrderActivity extends BaseActivity {

    private EditText mEdtOrder, mEdtMoney;
    private TextView mTxtGoods;

    //生成订单
    private String URL_TO_ORDER = "http://api.iyuce.com/v1/store/order";
    private String URL_TO_PAY = "http://api.iyuce.com/v1/store/pay";
    private String URL_TO_CASH_PAY = "http://api.iyuce.com/v1/store/paywithcash?paytype=alipay&id=";
    private String URL_TO_WXPAY = "http://api.iyuce.com/v1/store/paywithcash?paytype=wxapp&id=";
    private String URL_TO_VALID = "http://api.iyuce.com/v1/store/validpaybyapp?paytype=alipay";

    private String total_price, local_address_id, local_goods_name, local_skuids, local_store_user_money;
    private String local_user_id, local_order_no, local_order_id, local_alipay_data;

    private static final int SDK_PAY_FLAG = 2;

    private IWXAPI api;

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

                    LogUtil.i("resultStatus = " + resultStatus + ",resultMemo = " + resultMemo + ",resultInfo = " + resultInfo);
                    //请求验证阿里支付是否支付成功
                    validRequest(resultInfo);

                    LogUtil.i(resultInfo);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showMessage(StoreOrderActivity.this, "本地回调，支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showMessage(StoreOrderActivity.this, "本地回调,支付失败");
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

        //先进行微信注册
        api = WXAPIFactory.createWXAPI(this, "wxee1be723a57f9d21");
        initView();
    }

    private void initView() {
        local_user_id = PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("userId", "");

        //从订单列表或者从StorePay界面传过来的
        total_price = getIntent().getStringExtra("total_price");
        local_address_id = getIntent().getStringExtra("address");
        local_skuids = getIntent().getStringExtra("skuids");
        local_store_user_money = getIntent().getStringExtra("discount");
        local_goods_name = getIntent().getStringExtra("goods_name");
        //从订单列表Activity传过来的订单号，不为空则不用去请求生成订单
        local_order_id = getIntent().getStringExtra("local_order_id");
        local_order_no = getIntent().getStringExtra("local_order_no");

        mEdtOrder = (EditText) findViewById(R.id.edt_activity_storeorder_order);
        mEdtMoney = (EditText) findViewById(R.id.edt_activity_storeorder_money);
        mTxtGoods = (TextView) findViewById(R.id.txt_activity_storeorder_goods);

        mEdtMoney.setText(total_price);
        mTxtGoods.setText(local_goods_name.substring(1, local_goods_name.length() - 1));

        if (TextUtils.isEmpty(local_order_no)) {
            //生成订单请求
            requestOrder();
        } else {
            mEdtOrder.setText(local_order_no);
        }
    }

    /**
     * 生成订单
     */
    private void requestOrder() {
        StringRequest addressRequest = new StringRequest(Request.Method.POST, URL_TO_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("requestOrder = " + s);
                JSONObject obj;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("data");
                        mEdtOrder.setText(obj.getString("order_no"));
                        local_order_no = obj.getString("order_no");
                        local_order_id = obj.getString("id");
                        mEdtMoney.setText(obj.getString("actual_price"));
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
                map.put("userid", local_user_id);
                map.put("skuids", local_skuids);
                LogUtil.i("aaaa = " + local_store_user_money + "," + local_address_id + "," + local_skuids + ","
                        + local_user_id);
                return map;
            }
        };
        addressRequest.setTag("StoreOrderActivity");
        addressRequest.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppContext.getHttpQueue().add(addressRequest);
    }

    /**
     * 以上是生成订单部分，以下是支付部分
     *
     * @param view
     */

    //阿里支付
    public void payAli(View view) {
        toPay("alipay", URL_TO_CASH_PAY);
    }

    // 微信支付
    public void payWx(View view) {
        toPay("wxapp", URL_TO_WXPAY);
    }

    //检查微信版本是否支持支付
    public void toCheck(View view) {
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        ToastUtil.showMessage(this, "该版本微信是否支持支付 = " + String.valueOf(isPaySupported));
        startActivity(new Intent(this,WXPayEntryActivity.class));
    }

    /**
     * 支付请求第一步，金币支付
     */
    public void toPay(final String method, final String url) {
        StringRequest payRequest = new StringRequest(Request.Method.POST,
                URL_TO_PAY + "?id=" + local_order_id + "&userid=" + local_user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtil.i("toPay = " + s);
                        try {
                            JSONObject obj;
                            obj = new JSONObject(s);
                            if (obj.getString("code").equals("0")) {
                                int init_money = Integer.parseInt(PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("store_user_money", ""));
                                PreferenceUtil.save(StoreOrderActivity.this, "store_user_money", (init_money - Integer.parseInt(local_store_user_money)) + "");
                                startActivity(new Intent(StoreOrderActivity.this, MainActivity.class));
                            } else if (obj.getString("code").equals("2")) {
                                ToastUtil.showMessage(StoreOrderActivity.this, "金币不足抵扣,去调支付宝或者微信");
                                //现金支付请求
                                LogUtil.i("cashrequest url = " + url + local_order_id);
                                cashRequest(method, url + local_order_id);
                            } else {
                                ToastUtil.showMessage(StoreOrderActivity.this, "支付失败");
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
        });
        payRequest.setTag("StoreOrderActivity");
        payRequest.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppContext.getHttpQueue().add(payRequest);
    }

    /**
     * 支付请求第二步，现金支付宝支付
     */
    private void cashRequest(final String method, String url) {
        StringRequest cashRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("cashRequest = " + s);
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        //TODO 区分 微信和阿里支付
                        //如果是阿里支付
                        if (method.equals("alipay")) {
                            ToastUtil.showMessage(StoreOrderActivity.this, "去调支付宝吧 = ");
                            local_alipay_data = obj.getString("data");
                            //必须异步调用支付宝
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(StoreOrderActivity.this);
                                    Map<String, String> result = alipay.payV2(local_alipay_data, true);

                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }
                        //如果是微信支付
                        if (method.equals("wxapp")) {
                            String data = obj.getString("data");
                            obj = new JSONObject(data);
                            LogUtil.i("wx obj = " + obj);
                            PayReq req = new PayReq();
                            req.appId = "wxee1be723a57f9d21";
                            req.partnerId = obj.getString("partnerid");
                            req.prepayId = obj.getString("prepayid");
                            req.nonceStr = obj.getString("noncestr");
                            req.timeStamp = obj.getString("timestamp");
                            req.packageValue = obj.getString("package");
                            req.sign = obj.getString("sign");
                            // 调起请求之前先将该app注册到微信
                            boolean b = api.sendReq(req);
                            ToastUtil.showMessage(StoreOrderActivity.this, "去调微信支付吧 = " + b);
                        }
                    } else {
                        ToastUtil.showMessage(StoreOrderActivity.this, "调用支付宝或微信失败");
                        LogUtil.i(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("paytype", method);
                map.put("id", local_order_id);
                return map;
            }
        };
        cashRequest.setTag("StoreOrderActivity");
        cashRequest.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppContext.getHttpQueue().add(cashRequest);
    }

    /**
     * 校验支付宝回调结果
     */
    private void validRequest(final String pay_result) {
        StringRequest validRequest = new StringRequest(Request.Method.POST, URL_TO_VALID, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("valid s = " + s);
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreOrderActivity.this, "message" + obj.getString("message"));
                        int init_money = Integer.parseInt(PreferenceUtil.getSharePre(StoreOrderActivity.this).getString("store_user_money", ""));
                        PreferenceUtil.save(StoreOrderActivity.this, "store_user_money", (init_money - Integer.parseInt(local_store_user_money)) + "");
                        startActivity(new Intent(StoreOrderActivity.this, MainActivity.class));
                    } else {
                        ToastUtil.showMessage(StoreOrderActivity.this, "code !=0" + obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("volleyError = " + volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("", pay_result);
                LogUtil.i("pay_result = " + pay_result);
                return map;
            }
        };
        validRequest.setTag("validRequest");
        validRequest.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppContext.getHttpQueue().add(validRequest);
    }
}