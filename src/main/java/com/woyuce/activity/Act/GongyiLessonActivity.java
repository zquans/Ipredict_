package com.woyuce.activity.Act;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.GongyiLessonAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/21.
 */
public class GongyiLessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView txtback;
    private Button btnListening, btnSpeaking, btnReading, btnWritting;
    private ListView listview;

    private GongyiLessonAdapter adapter;
    private ArrayList<GongyiAudio> audioList = new ArrayList<>();
    private ArrayList<GongyiAudio> audioTypeList = new ArrayList<>();

    private String URL_LIST = "http://api.iyuce.com/v1/exam/audios";
    private String URL_TYPE = "http://api.iyuce.com/v1/exam/audiotypes";

    private static final int AUDIO_LIST = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUDIO_LIST:
                    adapter = new GongyiLessonAdapter(GongyiLessonActivity.this, (List<GongyiAudio>) msg.obj);
                    listview.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("audiolesson");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gongyilesson);

        initView();
        getTypeRequest(URL_TYPE);
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview_audiolesson);
        txtback = (TextView) findViewById(R.id.txt_audiolesson_back);
        btnListening = (Button) findViewById(R.id.btn_audiolesson_listening);
        btnSpeaking = (Button) findViewById(R.id.btn_audiolesson_reading);
        btnReading = (Button) findViewById(R.id.btn_audiolesson_speaking);
        btnWritting = (Button) findViewById(R.id.btn_audiolesson_writting);

        listview.setOnItemClickListener(this);
        txtback.setOnClickListener(this);
        btnListening.setOnClickListener(this);
        btnSpeaking.setOnClickListener(this);
        btnReading.setOnClickListener(this);
        btnWritting.setOnClickListener(this);
    }

    private void getListRequest(String url, final String type_id) {
        progressdialogshow(this);
        StringRequest audioListRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("response audiolist= " + response);
                try {
                    JSONObject obj;
                    JSONArray arr;
                    GongyiAudio audio;
                    obj = new JSONObject(response);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            audio = new GongyiAudio();
                            obj = arr.getJSONObject(i);
                            audio.setId(obj.getString("id"));
                            audio.setTitle(obj.getString("title"));
                            audio.setUrl(obj.getString("url"));
                            audioList.add(audio);
                        }
                        Message msg = new Message();
                        msg.what = AUDIO_LIST;
                        msg.obj = audioList;
                        mHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressdialogcancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String localtoken = PreferenceUtil.getSharePre(GongyiLessonActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("type_id", type_id);
                map.put("date", "");
                map.put("page", "1");
                return map;
            }
        };
        audioListRequest.setTag("audiolesson");
        AppContext.getHttpQueue().add(audioListRequest);
    }

    /**
     * 获取音频类型
     *
     * @param url
     */
    private void getTypeRequest(String url) {
        StringRequest audioTypeRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        arr = obj.getJSONArray("data");
                        GongyiAudio audio;
                        for (int i = 0; i < arr.length(); i++) {
                            audio = new GongyiAudio();
                            obj = arr.getJSONObject(i);
                            audio.setType_title(obj.getString("title"));
                            audio.setId(obj.getString("id"));
                            audioTypeList.add(audio);
                        }
                        getListRequest(URL_LIST, audioTypeList.get(0).getId());
                        btnListening.setText(audioTypeList.get(0).getType_title());
                        btnSpeaking.setText(audioTypeList.get(1).getType_title());
                        btnReading.setText(audioTypeList.get(2).getType_title());
                        btnWritting.setText(audioTypeList.get(3).getType_title());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String localtoken = PreferenceUtil.getSharePre(GongyiLessonActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }
        };
        audioTypeRequest.setTag("audiolesson");
        AppContext.getHttpQueue().add(audioTypeRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GongyiAudio audio = audioList.get(position);
        Intent intent = new Intent(this, GongyiContentActivity.class);
        intent.putExtra("url", audio.getUrl());
        intent.putExtra("title", audio.getTitle());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_audiolesson_back:
                finish();
                break;
            case R.id.btn_audiolesson_listening:
                chooseType(v, 0);
                break;
            case R.id.btn_audiolesson_reading:
                chooseType(v, 1);
                break;
            case R.id.btn_audiolesson_speaking:
                chooseType(v, 2);
                break;
            case R.id.btn_audiolesson_writting:
                chooseType(v, 3);
                break;
        }
    }

    private void chooseType(View v, int pos) {
        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
        mAnimator.setDuration(500).start();
        audioList.clear();
        getListRequest(URL_LIST, audioTypeList.get(pos).getId());
    }
}