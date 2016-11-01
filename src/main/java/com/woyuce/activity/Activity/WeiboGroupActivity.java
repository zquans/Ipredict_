package com.woyuce.activity.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/12.
 */
public class WeiboGroupActivity extends Activity implements AdapterView.OnItemClickListener {

    private String local_token;
    private String URL = "http://api.iyuce.com/v1/bbs/getcategories?categoryid=";

    private ListView mListView;
    private List<String> mDataList = new ArrayList<>();
    private ArrayAdapter mAdapter;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("weibogroup");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibogroup);

        initView();
    }

    private void initView() {
        local_token = getIntent().getStringExtra("local_token");

        mListView = (ListView) findViewById(R.id.listview_activity_weibogroup);
        mListView.setOnItemClickListener(this);
        requestJson();
    }


    /**
     * 请求数据
     */
    private void requestJson() {
        StringRequest weiboDataRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(response);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        LogUtil.e("arr = ?? " + arr);
                        String category_name;
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            category_name = obj.getString("category_name");
                            mDataList.add(category_name);
                        }
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + obj.getString("message"));
                    }
                    // 将数据放到适配器中
                    mAdapter = new ArrayAdapter(WeiboGroupActivity.this, android.R.layout.simple_list_item_1, mDataList);
                    mListView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + local_token);
                return headers;
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
////                map.put("", localsubid);
//                return map;
//            }
        };
        weiboDataRequest.setTag("weibogroup");
        AppContext.getHttpQueue().add(weiboDataRequest);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
