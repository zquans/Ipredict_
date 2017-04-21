package com.woyuce.activity.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.DbUtil;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_ORDER);
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
     */
    @Override
    public void onResp(BaseResp resp) {
        LogUtil.d("onPayFinish, errCode = " + resp.errCode);
        String for_wx_validate = PreferenceUtil.getSharePre(WXPayEntryActivity.this).getString("for_wx_validate", "");

        validRequest(resp, "{\"out_trade_no\":\"" + for_wx_validate + "\"}");
        LogUtil.d("ping = " + "{\"out_trade_no\":\"" + for_wx_validate + "\"}");
    }

    /**
     * 校验微信支付回调结果
     */
    private void validRequest(final BaseResp resp, final String pay_result) {
        HashMap<String, String> params = new HashMap<>();
        params.put("", pay_result);
        HttpUtil.post(Constants.URL_POST_STORE_ORDER_TO_WEXIN_VALID, params, Constants.ACTIVITY_STORE_ORDER, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                LogUtil.i("valid wxpay = " + result);
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
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
        });
    }

    /**
     * 删除数据库中的这张表
     */
    private void deleteSql() {
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, Constants.DATABASE_IYUCE).getWritableDatabase();
        mDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CART);
        mDatabase.close();
    }
}