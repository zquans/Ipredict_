package com.woyuce.activity.UI.Activity.Gongyi;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.Adapter.Gongyi.GongyiLessonAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Gongyi.GongyiAudio;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

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
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_AUDIO_LESSON);
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
        HttpHeaders headers = new HttpHeaders();
        String localtoken = PreferenceUtil.getSharePre(this).getString("localtoken", "");
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("type_id", type_id);
        params.put("date", "");
        params.put("page", page_num + "");
        OkGo.post(url).tag(Constants.ACTIVITY_AUDIO_LESSON).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doListSuccess(s, code);
                    }
                });
    }

    private void doListSuccess(String response, int code) {
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
    }

    private void getTypeRequest(String url) {
        OkGo.get(url).tag(Constants.ACTIVITY_AUDIO_LESSON)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doTypeSuccess(s);
                    }
                });
    }

    private void doTypeSuccess(String s) {
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

    @Override
    public void onClick(View v) {
        //TODO 多一个类型(四个按钮应该做成横向RecyclerView)
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