package com.woyuce.activity.Controller.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woyuce.activity.Adapter.Store.StorePayAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Login.LoginActivity;
import com.woyuce.activity.Model.Store.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.MathUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7
 */

public class StorePayActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mReLayoutAddress;
    private TextView mTxtAddressOne, mTxtAddressTwo, mTxtCount, mTxtPrice, mTxtFinalPrice, mTxtUserMoney;
    private EditText mEdtMiddle;

    private ListView mListView;
    private StorePayAdapter mAdapter;
    private ArrayList<StoreMenu> mStoreList = new ArrayList<>();

    //有关商品的列表
    ArrayList<String> mSpecNameList = new ArrayList<>();
    private Integer total_count;
    private Double total_price;
    private Double local_store_user_money, max_store_user_money;

    //获取默认收货地址
//    private String URL = "http://api.iyuce.com/v1/store/getdefaultaddr";
    private String local_skuids = "";
    private boolean bIsAddress = false;

    public void back(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_pay);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        AppContext.getHttpQueue().cancelAll("StorePayRequest");
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_PAY);
    }

    private void initView() {
        //获取到传递来的数据
        Intent intent = getIntent();
        mStoreList = (ArrayList<StoreMenu>) intent.getSerializableExtra("mStoreList");
        total_count = intent.getIntExtra("total_count", 10000);
        total_price = intent.getDoubleExtra("total_price", 1000000.00);

        for (int i = 0; i < mStoreList.size(); i++) {
            local_skuids = local_skuids + mStoreList.get(i).getGoodsskuid() + "|" + mStoreList.get(i).getNum() + ",";
            mSpecNameList.add(mStoreList.get(i).getName() + "_(" + mStoreList.get(i).getSpecname() + ") \r\rX\r" + mStoreList.get(i).getNum() + "件");
        }

        mReLayoutAddress = (RelativeLayout) findViewById(R.id.relative_activity_storepay);
        mReLayoutAddress.setOnClickListener(this);
        mTxtAddressOne = (TextView) findViewById(R.id.txt_activity_storepay_defoultaddress_one);
        mTxtAddressTwo = (TextView) findViewById(R.id.txt_activity_storepay_defoultaddress_two);
        mTxtCount = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);
        mTxtUserMoney = (TextView) findViewById(R.id.txt_actvity_storepay_havemoney);
        mEdtMiddle = (EditText) findViewById(R.id.edt_actvity_storepay_middle);
        mTxtPrice.setText(total_price + "元");
        mTxtCount.setText(total_count + "件");

        mListView = (ListView) findViewById(R.id.listview_actvity_storepay);
        mAdapter = new StorePayAdapter(this, mStoreList);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        //请求金币
        requestMoney();
        //请求地址
        requestAddress();
    }

    /**
     * 帮助类:动态设置ListView的高度
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     * 获取金币请求
     */
    private void requestMoney() {
        HttpUtil.get(Constants.URL_MONEY_INFO + PreferenceUtil.getSharePre(StorePayActivity.this).getString("userId", "")
                , Constants.ACTIVITY_STORE_PAY, new RequestInterface() {
                    @Override
                    public void doSuccess(String result) {
                        try {
                            JSONObject obj;
                            obj = new JSONObject(result);
                            if (obj.getString("code").equals("0")) {
                                local_store_user_money = Double.parseDouble(obj.getString("data"));
                                max_store_user_money = local_store_user_money;
                                mTxtUserMoney.setText("你有" + local_store_user_money + "个金币,可以抵扣" + local_store_user_money + "元");
                                //判断该显示的金币数和该显示的实际应付价格,最大金币数不应超过价格
                                if (local_store_user_money > total_price) {
                                    local_store_user_money = total_price;
                                }
                                mEdtMiddle.setText(local_store_user_money.toString());
                                mTxtFinalPrice.setText("应付总金额:" + MathUtil.sub(total_price, local_store_user_money) + "元");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 请求收货地址
     */
    private void requestAddress() {
        progressdialogshow(this);
        HttpUtil.get(Constants.URL_GET_STORE_PAY + "?userid=" + PreferenceUtil.getSharePre(this).getString("userId", "")
                , Constants.ACTIVITY_STORE_PAY, new RequestInterface() {
                    @Override
                    public void doSuccess(String result) {
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(result);
                            arr = obj.getJSONArray("address");
                            if (arr.length() == 0) {
                                mTxtAddressTwo.setText("去新增地址");
                                mTxtAddressTwo.setGravity(Gravity.CENTER_HORIZONTAL);
                            } else {
                                bIsAddress = true;
                                obj = arr.getJSONObject(0);
                                mTxtAddressOne.setText(obj.getString("name") + "\r\r" + obj.getString("mobile"));
                                mTxtAddressTwo.setText("QQ:" + obj.getString("q_q") + "\r\r" + "邮箱" + obj.getString("email"));
                                local_address_id = obj.getString("id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressdialogcancel();
                    }
                });
    }

    public void plusMoney(View view) {
        if (max_store_user_money <= local_store_user_money) {
            ToastUtil.showMessage(this, "您的金币不够咯");
            return;
        }
        if (total_price <= local_store_user_money) {
            ToastUtil.showMessage(this, "您的金币足够抵扣啦");
            return;
        }
        local_store_user_money = MathUtil.add(local_store_user_money, (double) 1);
        mEdtMiddle.setText(local_store_user_money.toString());
        mTxtFinalPrice.setText("应付总金额:" + MathUtil.sub((double) total_price, (double) local_store_user_money) + "元");
    }

    public void minusMoney(View view) {
        if (local_store_user_money < 1) {
            ToastUtil.showMessage(this, "不能再减啦");
            return;
        }
        local_store_user_money = MathUtil.sub(local_store_user_money, (double) 1);
        mEdtMiddle.setText(local_store_user_money.toString());
        mTxtFinalPrice.setText("应付总金额:" + MathUtil.sub((double) total_price, (double) local_store_user_money) + "元");
    }

    private String local_address_id;

    public void nowPay(View view) {
        if (total_price == 0) {
            ToastUtil.showMessage(this, "快去添加商品吧");
            return;
        }
        if (!bIsAddress) {
            ToastUtil.showMessage(this, "无效的联系人信息，请检查收货信息是否有误");
            return;
        }
        if (TextUtils.isEmpty(PreferenceUtil.getSharePre(this).getString("userId", null))) {
            new AlertDialog.Builder(this)
                    .setTitle("您还没有登陆哦")
                    .setMessage("立刻去登陆吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(StorePayActivity.this, LoginActivity.class));
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return;
        }
        Intent intent = new Intent(this, StoreOrderActivity.class);
        intent.putExtra("total_price", total_price.toString());
        intent.putExtra("address", local_address_id);
        intent.putExtra("skuids", local_skuids);
        intent.putExtra("discount", String.valueOf(Math.ceil(Double.parseDouble(mEdtMiddle.getText().toString()))));
        intent.putExtra("goods_name", mSpecNameList.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("resultCode = " + resultCode);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE_FOR_ADDRESS:
                mTxtAddressOne.setText(data.getStringExtra("default_address_name") + "\r\r" + data.getStringExtra("default_address_mobile"));
                mTxtAddressTwo.setText(data.getStringExtra("default_address_q_q") + "\r\r" + data.getStringExtra("default_address_email"));
                mTxtAddressTwo.setGravity(Gravity.CENTER_VERTICAL);
                local_address_id = data.getStringExtra("default_address_id");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_activity_storepay:
                startActivityForResult(new Intent(this, StoreAddressActivity.class), Constants.REQUEST_CODE_FOR_ADDRESS);
                break;
        }
    }
}