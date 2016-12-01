package com.woyuce.activity.Act;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.woyuce.activity.Utils.LogUtil;
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
    private TextView mTxtAddressOne, mTxtAddressTwo, mTxtPrice, mTxtCount, mTxtUserMoney;
    private EditText mEdtMiddle;

    private ListView mListView;
    private StorePayAdapter mAdapter;
    private ArrayList<StoreMenu> mList = new ArrayList<>();

    //有关商品的列表
    ArrayList<String> mGoodsSkuIdList = new ArrayList<>();
    ArrayList<String> mNameList = new ArrayList<>();
    ArrayList<String> mPriceList = new ArrayList<>();
    ArrayList<String> mNumList = new ArrayList<>();
    ArrayList<String> mSpecNameList = new ArrayList<>();
    private Integer total_count;
    private Double total_price;
    private Integer local_store_user_money, max_store_user_money;

    //获取默认收货地址
    private String URL = "http://api.iyuce.com/v1/store/getdefaultaddr";

    public void back(View view) {
        finish();
    }

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
        mSpecNameList = intent.getStringArrayListExtra("mSpecNameList");
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
        mEdtMiddle = (EditText) findViewById(R.id.edt_actvity_storepay_middle);
        mTxtPrice.setText(total_price + "元");
        mTxtCount.setText(total_count + "件");

        local_store_user_money = Integer.parseInt(PreferenceUtil.getSharePre(this).getString("store_user_money", ""));
        max_store_user_money = local_store_user_money;
        mTxtUserMoney.setText("你有" + local_store_user_money + "个金币,可以抵扣" + local_store_user_money + "元");
        mEdtMiddle.setText(local_store_user_money.toString());

        mEdtMiddle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.parseInt(s.toString()) - local_store_user_money > 0) {
                    ToastUtil.showMessage(StorePayActivity.this, "您的金币不足");
                    mEdtMiddle.setText("1");
                }
            }
        });

        StoreMenu storeMenu;
        for (int i = 0; i < mGoodsSkuIdList.size(); i++) {
            storeMenu = new StoreMenu();
            storeMenu.setGoodsskuid(mGoodsSkuIdList.get(i));
            storeMenu.setName(mNameList.get(i));
            storeMenu.setSpecname(mSpecNameList.get(i));
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
        StringRequest addressRequest = new StringRequest(Request.Method.GET,
                URL + "?userid=" + PreferenceUtil.getSharePre(this).getString("userId", ""), new Response.Listener<String>() {
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
                        local_address_id = obj.getString("id");
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
        mEdtMiddle.setText(local_store_user_money.toString());
    }

    public void minusMoney(View view) {
        if (local_store_user_money <= 0) {
            ToastUtil.showMessage(this, "不能再减啦");
            return;
        }
        local_store_user_money = local_store_user_money - 1;
        mEdtMiddle.setText(local_store_user_money.toString());
    }

    private String local_address_id;

    public void nowPay(View view) {
        String local_skuids = "";
        for (int i = 0; i < mList.size(); i++) {
            local_skuids = local_skuids + mList.get(i).getGoodsskuid()
                    + "|" + mList.get(i).getNum() + ",";
        }
        Intent intent = new Intent(this, StoreOrderActivity.class);
        intent.putExtra("total_price", total_price.toString());
        intent.putExtra("address", local_address_id);
        intent.putExtra("skuids", local_skuids);
        intent.putExtra("discount", mEdtMiddle.getText().toString());
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
            case REQUEST_CODE_FOR_ADDRESS:
                mTxtAddressOne.setText(data.getStringExtra("default_address_name") + "\r\r" + data.getStringExtra("default_address_mobile"));
                mTxtAddressTwo.setText(data.getStringExtra("default_address_q_q") + "\r\r" + data.getStringExtra("default_address_email"));
                mTxtAddressTwo.setGravity(Gravity.CENTER_VERTICAL);
                local_address_id = data.getStringExtra("default_address_id");
                break;
        }
    }

    //startActivityForResult的请求码
    private static final int REQUEST_CODE_FOR_ADDRESS = 0x001;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_activity_storepay:
                startActivityForResult(new Intent(this, StoreAddressActivity.class), REQUEST_CODE_FOR_ADDRESS);
                break;
        }
    }
}