package com.woyuce.activity.UI.Activity.Writting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.Adapter.Writting.WitSearchAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Writting.WitSearch;
import com.woyuce.activity.R;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class WitSearchActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private TextView txtResult;
    private Button btnBack;
    private ImageView mImgView;
    private ListView witsearchlistview;

    private String localkey, localid, localsubid;
    private ArrayList<WitSearch> witsearchList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_WIT_SEARCH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witsearch);

        initView();
        getJson();
    }

    private void initView() {
        Intent it_search = getIntent();
        localkey = it_search.getStringExtra("localkey");
        localid = it_search.getStringExtra("localid");

        txtResult = (TextView) findViewById(R.id.txt_witsearch_result);
        btnBack = (Button) findViewById(R.id.btn_witsearch_back);
        mImgView = (ImageView) findViewById(R.id.arrow_back);
        witsearchlistview = (ListView) findViewById(R.id.listview_witsearch);

        witsearchlistview.setOnItemClickListener(this);
        mImgView.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void getJson() {
        HttpParams params = new HttpParams();
        params.put("key", localkey);
        if (localid != null) {
            params.put("categoryid", localid);
        }
        OkGo.post(Constants.URL_POST_WRITTING_SEARCH).tag(Constants.ACTIVITY_WIT_SEARCH).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        doSuccess(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        txtResult.setText("没有找到您要的结果呢，亲...");
                    }
                });
    }

    private void doSuccess(String response) {
        WitSearch witsearch;
        JSONObject jsonobj;
        try {
            jsonobj = new JSONObject(response);
            int result = jsonobj.getInt("code");
            if (result == 0) {
                JSONArray data = jsonobj.getJSONArray("data");
                if (data.length() == 0) {
                    txtResult.setText("没有找到您要的结果呢，亲...");
                }
                for (int i = 0; i < data.length(); i++) {
                    jsonobj = data.getJSONObject(i);
                    witsearch = new WitSearch();
                    witsearch.subid = jsonobj.getString("subid");
                    witsearch.subname = jsonobj.getString("subname");
                    witsearchList.add(witsearch);
                }
                WitSearchAdapter adapter = new WitSearchAdapter(WitSearchActivity.this, witsearchList);
                witsearchlistview.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_witsearch_back:
                finish();
                break;
            case R.id.arrow_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WitSearch witsearch = witsearchList.get(position);
        localsubid = witsearch.subid;
        Intent intent = new Intent(this, WitContentActivity.class);
        intent.putExtra("localsubid", localsubid);
        startActivity(intent);
    }
}