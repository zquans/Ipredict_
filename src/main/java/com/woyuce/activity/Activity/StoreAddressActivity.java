package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
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
public class StoreAddressActivity extends BaseActivity {

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mList = new ArrayList<>();

    private String URL = "http://api.iyuce.com/v1/store/findbyuser";
    private String URL_OPERA = "http://api.iyuce.com/v1/store/OperationAddress?operation={operation}&id={id}&userid={userid}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeaddress);

        initView();
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_actvity_store_address);

        requestAddressList();
    }

    private void requestAddressList() {
        URL = URL + "?userid=" + PreferenceUtil.getSharePre(this).getString("userId", "");
        StringRequest addressListRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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
                            //TODO 地址拆解
                            mList.add("");
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
        ToastUtil.showMessage(this, "新增地址");
        startActivity(new Intent(this,StoreAddAddressActivity.class));
    }
}