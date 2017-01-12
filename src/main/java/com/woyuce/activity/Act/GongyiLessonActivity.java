package com.woyuce.activity.Act;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.woyuce.activity.Adapter.GongyiLessonAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.RecyclerItemClickListener;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/21.
 */
public class GongyiLessonActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener {

    private TextView txtback;
    private Button btnListening, btnSpeaking, btnReading, btnWritting;
    private XRecyclerView recyclerview;

    private GongyiLessonAdapter adapter;
    private ArrayList<GongyiAudio> audioList = new ArrayList<>();
    private ArrayList<GongyiAudio> audioTypeList = new ArrayList<>();

    private static final int GET_DATA_OK = 2;    //获取数据
    private static final int LOAD_MORE_DATA_OK = 1; //加载更多数据
    private int page_num = 1;
    private int local_type = 0;
    private boolean isRefresh = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA_OK:
                    adapter.notifyDataSetChanged();
                    recyclerview.refreshComplete();
                    break;
                case LOAD_MORE_DATA_OK:
                    adapter.notifyDataSetChanged();
                    recyclerview.loadMoreComplete();
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
        getTypeRequest(Constants.URL_GET_AUDIO_TYPE);
    }

    private void initView() {
        txtback = (TextView) findViewById(R.id.txt_audiolesson_back);
        btnListening = (Button) findViewById(R.id.btn_audiolesson_listening);
        btnSpeaking = (Button) findViewById(R.id.btn_audiolesson_reading);
        btnReading = (Button) findViewById(R.id.btn_audiolesson_speaking);
        btnWritting = (Button) findViewById(R.id.btn_audiolesson_writting);
        recyclerview = (XRecyclerView) findViewById(R.id.xrecyclerview_audiolesson);
        adapter = new GongyiLessonAdapter(GongyiLessonActivity.this, audioList);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);
        recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallGridBeat);
        recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview.setLoadingListener(this);
        txtback.setOnClickListener(this);
        btnListening.setOnClickListener(this);
        btnSpeaking.setOnClickListener(this);
        btnReading.setOnClickListener(this);
        btnWritting.setOnClickListener(this);
    }

    private void getListRequest(final int code, String url, final String type_id, final int page_num) {
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
                        if (isRefresh) {
                            audioList.clear();
                        }
                        for (int i = 0; i < arr.length(); i++) {
                            audio = new GongyiAudio();
                            obj = arr.getJSONObject(i);
                            audio.setId(obj.getString("id"));
                            audio.setTitle(obj.getString("title"));
                            audio.setUrl(obj.getString("url"));
                            audioList.add(audio);
                        }
                        Message msg = new Message();
                        msg.obj = audioList;
                        if (code == GET_DATA_OK) {
                            msg.what = GET_DATA_OK;
                            mHandler.sendMessage(msg);
                        }
                        if (code == LOAD_MORE_DATA_OK) {
                            msg.what = LOAD_MORE_DATA_OK;
                            mHandler.sendMessageDelayed(msg, 100);
                        }
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
                map.put("page", page_num + "");
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
                        getListRequest(GET_DATA_OK, Constants.URL_POST_AUDIO_LIST, audioTypeList.get(0).getId(), page_num);
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
        //指定获取的音频类型
        local_type = pos;
        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
        mAnimator.setDuration(500).start();
        audioList.clear();
        page_num = 1;
        getListRequest(GET_DATA_OK, Constants.URL_POST_AUDIO_LIST, audioTypeList.get(pos).getId(), page_num);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page_num = 1;
        getListRequest(GET_DATA_OK, Constants.URL_POST_AUDIO_LIST, audioTypeList.get(local_type).getId(), page_num);
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        page_num++;
        getListRequest(LOAD_MORE_DATA_OK, Constants.URL_POST_AUDIO_LIST, audioTypeList.get(local_type).getId(), page_num);
    }
}