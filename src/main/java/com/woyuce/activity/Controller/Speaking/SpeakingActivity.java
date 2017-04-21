package com.woyuce.activity.Controller.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.woyuce.activity.Adapter.Speaking.SpeakingAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Speaking.SpeakingBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout llStatis;
    private ImageView mImgBack;

    private Button btnShare;
    private ListView mListView;

    private List<SpeakingBean> speakingList = new ArrayList<>();
    private SpeakingAdapter adapter;

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_SPEAKING);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        speakingList.clear();
        adapter.notifyDataSetChanged();
        requestJson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);

        initView();
        requestJson();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llStatis = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnShare = (Button) findViewById(R.id.button_speaking_share);
        mListView = (ListView) findViewById(R.id.listview_speaking_vote);

        llStatis.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void requestJson() {
        HttpUtil.get(Constants.URL_POST_SPEAKING, Constants.ACTIVITY_SPEAKING, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    SpeakingBean speaking;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
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
                        adapter = new SpeakingAdapter(SpeakingActivity.this, speakingList);
                        mListView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        Intent intent = new Intent(this, SpeakingDetailActivity.class);
        intent.putExtra("SpeakingBean", speakingList.get(position));
        startActivity(intent);
    }
}