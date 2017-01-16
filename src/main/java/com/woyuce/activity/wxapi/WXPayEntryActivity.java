package com.woyuce.activity.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.woyuce.activity.UI.Activity.MainActivity;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private Button mBtn;
    private TextView mTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_result);

        mTxt = (TextView) findViewById(R.id.txt_activity_wxpay);
        mBtn = (Button) findViewById(R.id.btn_activity_wxpay);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXPayEntryActivity.this.finish();
            }
        });
        api = WXAPIFactory.createWXAPI(this, "wxee1be723a57f9d21");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("validWxPayRequest");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        //这里面是什么？
        LogUtil.e("BaseReq = " + req.getType() + "||" + req.transaction + "||" + req.openId);
    }

    /**
     * 微信支付回调
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        LogUtil.d("onPayFinish, errCode = " + resp.errCode);
        String for_wx_validate = PreferenceUtil.getSharePre(WXPayEntryActivity.this).getString("for_wx_validate", "");

        validRequest(resp, "{\"out_trade_no\":\"" + for_wx_validate + "\"}");
        LogUtil.d("ping = " + "{\"out_trade_no\":\"" + for_wx_validate + "\"}");
    }

    private String URL_TO_VALID = "http://api.iyuce.com/v1/store/validpaybyapp?paytype=wxapp";

    /**
     * 校验微信支付回调结果
     */
    private void validRequest(final BaseResp resp, final String pay_result) {
        StringRequest validRequest = new StringRequest(Request.Method.POST, URL_TO_VALID, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("valid wxpay = " + s);
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(WXPayEntryActivity.this, "message" + obj.getString("message"));
                        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                            if (resp.errCode == 0) {
                                mTxt.setText("支付成功");//************************************
                                //支付成功
                                mBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //删除该数据表
                                        deleteSql();
                                        PreferenceUtil.removestoretbisexist(WXPayEntryActivity.this);
                                        //跳转到商城首页,或其他页
                                        startActivity(new Intent(WXPayEntryActivity.this, MainActivity.class));
                                        WXPayEntryActivity.this.finish();
                                    }
                                });
                            }
                            if (resp.errCode == -1) {
                                mTxt.setText("支付失败");//************************************
                                //支付失败
                                mBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        WXPayEntryActivity.this.finish();
                                    }
                                });
                            }
                            if (resp.errCode == -2) {
                                mTxt.setText("支付取消");//************************************
                                //支付取消
                                mBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        WXPayEntryActivity.this.finish();
                                    }
                                });
                            }
                        }
                    } else {
                        //客户端不确定，后台反馈直接不成功
                        mTxt.setText("支付失败");
                        mBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WXPayEntryActivity.this.finish();
                            }
                        });
                        LogUtil.i("code !=0" + obj.getString("message"));
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
        validRequest.setTag("validWxPayRequest");
        validRequest.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppContext.getHttpQueue().add(validRequest);
    }

    /**
     * 删除数据库中的这张表
     */
    private void deleteSql() {
        SQLiteDatabase mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        mDatabase.execSQL("drop table storetb");
        mDatabase.close();
    }
}