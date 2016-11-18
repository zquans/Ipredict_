package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.StorePayAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StorePayActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mReLayoutAddress;
    private TextView mTxtAddressOne, mTxtAddressTwo, mTxtPrice, mTxtCount, mTxtUserMoney, mTxtMiddle;

    private ListView mListView;
    private StorePayAdapter mAdapter;
    private ArrayList<StoreMenu> mList = new ArrayList<>();

    //有关商品的列表
    ArrayList<String> mGoodsSkuIdList = new ArrayList<>();
    ArrayList<String> mNameList = new ArrayList<>();
    ArrayList<String> mPriceList = new ArrayList<>();
    ArrayList<String> mNumList = new ArrayList<>();
    private Integer total_count;
    private Double total_price;
    private Integer local_store_user_money, max_store_user_money;

    //获取默认收货地址
    private String URL = "http://api.iyuce.com/v1/store/getdefaultaddr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storepay);

        initView();
    }

    private void initView() {
        //获取到传递来的数据
        Intent intent = getIntent();
        mGoodsSkuIdList = intent.getStringArrayListExtra("mGoodsSkuIdList");
        mNameList = intent.getStringArrayListExtra("mNameList");
        mPriceList = intent.getStringArrayListExtra("mPriceList");
        mNumList = intent.getStringArrayListExtra("mNumList");
        total_count = intent.getIntExtra("total_count", -1);
        total_price = intent.getDoubleExtra("total_price", 0.00);

        mReLayoutAddress = (RelativeLayout) findViewById(R.id.relative_activity_storepay);
        mReLayoutAddress.setOnClickListener(this);
        mTxtAddressOne = (TextView) findViewById(R.id.txt_activity_storepay_defoultaddress_one);
        mTxtAddressTwo = (TextView) findViewById(R.id.txt_activity_storepay_defoultaddress_two);
        mTxtPrice = (TextView) findViewById(R.id.txt_storecar_final_price);
        mTxtCount = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtUserMoney = (TextView) findViewById(R.id.txt_actvity_storepay_havemoney);
        mTxtMiddle = (TextView) findViewById(R.id.txt_actvity_storepay_middle);
        mTxtPrice.setText(total_price + "元");
        mTxtCount.setText(total_count + "件");

        local_store_user_money = Integer.parseInt(PreferenceUtil.getSharePre(this).getString("store_user_money", ""));
        max_store_user_money = local_store_user_money;
        mTxtUserMoney.setText("你有" + local_store_user_money + "个金币,可以抵扣" + local_store_user_money + "元");
        mTxtMiddle.setText(local_store_user_money.toString());

        StoreMenu storeMenu;
        for (int i = 0; i < mGoodsSkuIdList.size(); i++) {
            storeMenu = new StoreMenu();
            storeMenu.setGoodsskuid(mGoodsSkuIdList.get(i));
            storeMenu.setName(mNameList.get(i));
            storeMenu.setNum(mNumList.get(i));
            storeMenu.setPrice(mPriceList.get(i));
            mList.add(storeMenu);
        }

        mListView = (ListView) findViewById(R.id.listview_actvity_storepay);
        mAdapter = new StorePayAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        requestAddress();
    }

    /**
     * 帮助类:动态设置ListView的高度
     *
     * @param listView
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
                        mTxtAddressTwo.setText("去新增地址");
                        mTxtAddressTwo.setGravity(Gravity.CENTER_HORIZONTAL);
                    } else {
                        obj = arr.getJSONObject(0);
                        mTxtAddressOne.setText(obj.getString("name") + "\r\r" + obj.getString("mobile"));
                        mTxtAddressTwo.setText("QQ:" + obj.getString("q_q") + "\r\r" + "邮箱" + obj.getString("email"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        addressRequest.setTag("addressRequest");
        AppContext.getHttpQueue().add(addressRequest);
    }

    public void plusMoney(View view) {
        if (max_store_user_money <= local_store_user_money) {
            ToastUtil.showMessage(this, "您的金币不够咯");
            return;
        }
        local_store_user_money = local_store_user_money + 1;
        mTxtMiddle.setText(local_store_user_money.toString());
    }

    public void minusMoney(View view) {
        if (local_store_user_money <= 0) {
            ToastUtil.showMessage(this, "不能再减啦");
            return;
        }
        local_store_user_money = local_store_user_money - 1;
        mTxtMiddle.setText(local_store_user_money.toString());
    }

    public void nowPay(View view) {
        ToastUtil.showMessage(this, "去调支付吧,你要付这么多钱" + total_price + "元");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_activity_storepay:
                //TODO 应该做forResult，因为设置的默认地址应该返回
                startActivity(new Intent(this, StoreAddressActivity.class));
                break;
        }
    }
}