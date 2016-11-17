package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.StoreAddressAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreAddress;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/14.
 */
public class StoreAddressActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private StoreAddressAdapter mAdapter;
    private ArrayList<StoreAddress> mList = new ArrayList<>();

    private String URL = "http://api.iyuce.com/v1/store/findbyuser";

    @Override
    protected void onRestart() {
        super.onRestart();
        mList.clear();
        requestAddressList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeaddress);

        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_actvity_store_address);
        mListView.setOnItemClickListener(this);

        requestAddressList();
    }

    private void requestAddressList() {
        StringRequest addressListRequest = new StringRequest(Request.Method.GET, URL
                + "?userid=" + PreferenceUtil.getSharePre(this).getString("userId", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        arr = obj.getJSONArray("address");
                        if (arr.length() == 0) {
                            ToastUtil.showMessage(StoreAddressActivity.this, "还没有默认地址，去添加一个吧");
                        } else {
                            LogUtil.i("mList = " + arr.toString());
                            StoreAddress storeaddress;
                            for (int i = 0; i < arr.length(); i++) {
                                obj = arr.getJSONObject(i);
                                storeaddress = new StoreAddress();
                                storeaddress.setName(obj.getString("name"));
                                storeaddress.setMobile(obj.getString("mobile"));
                                storeaddress.setQ_q(obj.getString("q_q"));
                                storeaddress.setEmail(obj.getString("email"));
                                storeaddress.setId(obj.getString("id"));
                                storeaddress.setIs_default(obj.getString("is_default"));
                                storeaddress.setMobile_veri_code_id(obj.getString("mobile_veri_code_id"));
                                mList.add(storeaddress);
                            }
                            mAdapter = new StoreAddressAdapter(StoreAddressActivity.this, mList);
                            mListView.setAdapter(mAdapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        addressListRequest.setTag("addressListRequest");
        AppContext.getHttpQueue().add(addressListRequest);
    }

    public void add(View view) {
        startActivity(new Intent(this, StoreAddAddressActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(StoreAddressActivity.this, StoreAddAddressActivity.class);
        intent.putExtra("local_name", mList.get(position).getName());
        intent.putExtra("local_mobile", mList.get(position).getMobile());
        intent.putExtra("local_qq", mList.get(position).getQ_q());
        intent.putExtra("local_email", mList.get(position).getEmail());
        intent.putExtra("local_id", mList.get(position).getId());
        intent.putExtra("local_mobile_veri_code_id", mList.get(position).getMobile_veri_code_id());
        startActivity(intent);
    }
}