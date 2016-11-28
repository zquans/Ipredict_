package com.woyuce.activity.Act;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.Bean.StoreOrder;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/25.
 */
public class StoreOrderListActivity extends BaseActivity {

    private ListView mListView;
    private ArrayList<StoreOrder> mList = new ArrayList<>();
    private ArrayList<String> mmList = new ArrayList<>();
    private ArrayAdapter madapter;

    private String URL = "http://api.iyuce.com/v1/store/orderlist?userid=";

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("StoreOrderList");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_orderlist);

        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_activity_store_orderlist);

        requestData();
    }

    private void requestData() {
        StringRequest orderListRequest = new StringRequest(Request.Method.GET,
                URL + PreferenceUtil.getSharePre(this).getString("userId", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("s = " + s);
                try {
                    JSONObject obj;
                    JSONArray arr;
                    JSONObject obj_;
                    JSONArray arr_;
                    StoreOrder order;
                    ArrayList<StoreGoods> mArrayList = new ArrayList<>();
                    StoreGoods goods;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("orderlist");
                        arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            order = new StoreOrder();
                            obj = arr.getJSONObject(i);
                            order.setOrder_no(obj.getString("order_no"));
                            order.setPrice(obj.getString("price"));
                            order.setCreate_at(obj.getString("create_at"));

                            //StoreOrder对象内的StoreGoods数组
                            arr_ = obj.getJSONArray("user_order_details");
                            for (int j = 0; j < arr_.length(); j++) {
                                goods = new StoreGoods();
                                obj_ = arr_.getJSONObject(j);
                                goods.setThumb_img(obj_.getString("goods_thumb_img_url"));
                                goods.setGoods_title(obj_.getString("goods_title"));
                                mArrayList.add(goods);
                            }
                            order.setUser_order_details(mArrayList);
                            mList.add(order);
                        }
                        LogUtil.i("mList = " + mList);
                        mmList.add(mList.toString());
                        madapter = new ArrayAdapter(StoreOrderListActivity.this, android.R.layout.simple_list_item_1, mmList);
                        mListView.setAdapter(madapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        orderListRequest.setTag("StoreOrderList");
        AppContext.getHttpQueue().add(orderListRequest);
    }
}