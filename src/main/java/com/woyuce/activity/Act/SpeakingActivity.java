package com.woyuce.activity.Act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.SpeakingAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.SpeakingBean;
import com.woyuce.activity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout llStatis;
    private ImageView mImgBack;

    private Button btnShare;
    private ListView mListView;

    private String URL = "http://iphone.ipredicting.com/getvoteMge.aspx";
    private List<SpeakingBean> speakingList = new ArrayList<>();
    private SpeakingAdapter adapter;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("speaking");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        speakingList.clear();
        adapter.notifyDataSetChanged();
        getJson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);

        initView();
        getJson();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llStatis = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnShare = (Button) findViewById(R.id.button_speaking_share);
        mListView = (ListView) findViewById(R.id.listview_speaking_vote);
        adapter = new SpeakingAdapter(SpeakingActivity.this, speakingList);
        mListView.setAdapter(adapter);

        llStatis.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void getJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                SpeakingBean speaking;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            speaking = new SpeakingBean();
                            speaking.uname = jsonObject.getString("uname");
                            speaking.message = jsonObject.getString("message");
                            speaking.examroom = jsonObject.getString("examroom");
                            speaking.vtime = jsonObject.getString("vtime");
                            speakingList.add(speaking);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        stringRequest.setTag("speaking");
        AppContext.getHttpQueue().add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_speaking_share:
                startActivity(new Intent(this, SpeakingShare1Activity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.ll_speaking_stastis:
                startActivity(new Intent(this, SpeakingStatisActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingBean localspeaking = speakingList.get(position);
        Intent intent = new Intent(this, SpeakingDetailActivity.class);
        intent.putExtra("localspeaking", localspeaking);
        startActivity(intent);
    }
}