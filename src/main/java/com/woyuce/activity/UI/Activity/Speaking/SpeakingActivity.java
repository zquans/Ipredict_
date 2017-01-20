package com.woyuce.activity.UI.Activity.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.woyuce.activity.Adapter.Speaking.SpeakingAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Speaking.SpeakingBean;
import com.woyuce.activity.R;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
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
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SPEAKING);
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

        llStatis.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void getJson() {
        OkGo.post(Constants.URL_POST_SPEAKING).tag(Constants.ACTIVITY_SPEAKING)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
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
                adapter = new SpeakingAdapter(SpeakingActivity.this, speakingList);
                mListView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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