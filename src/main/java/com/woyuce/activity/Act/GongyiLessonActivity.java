package com.woyuce.activity.Act;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.GongyiLessonAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class GongyiLessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView listview;
    private TextView txtback;
    private Button btnListening, btnSpeaking, btnReading, btnWritting;

    private List<GongyiAudio> audioList = new ArrayList<>();
    private String URL_LIST = "http://php.ipredicting.com/service/audiolistingroup.php";
    private String URL_DETAIL = "http://php.ipredicting.com/service/audiodetail.php?id=";

    private String localAudioUrl, localAudioTitle;
    private GongyiLessonAdapter adapter;

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
        getListJson(URL_LIST);
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

    private void getListJson(String url) {
        progressdialogshow(this);
        StringRequest strinrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray data;
                GongyiAudio audio;
                LogUtil.e("response audiolist= " + response);
                try {
                    String parseString = new String(response.getBytes("ISO-8859-1"), "utf-8");
                    data = new JSONArray(parseString);
                    for (int i = 0; i < data.length(); i++) {
                        audio = new GongyiAudio();
                        obj = data.getJSONObject(i);
                        audio.id = obj.getString("id");
                        audio.title = obj.getString("title");
                        audioList.add(audio);
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                adapter = new GongyiLessonAdapter(GongyiLessonActivity.this, audioList);
                listview.setAdapter(adapter);
                progressdialogcancel();
            }
        }, errorback());
        strinrequest.setTag("audiolesson");
        AppContext.getHttpQueue().add(strinrequest);
    }

    // 抽出的方法，通过Item事件选择,拿到ID后，传入参数，执行该方法，成功则进入下一个Activity，并附带AudioUrl
    private void getDetailJson(String url) {
        StringRequest strinrequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    String parseString = new String(response.getBytes("ISO-8859-1"), "utf-8");
                    obj = new JSONObject(parseString);
                    localAudioUrl = obj.getString("url");
                    localAudioTitle = obj.getString("title");
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Intent it = new Intent(GongyiLessonActivity.this, GongyiContentActivity.class);
                it.putExtra("url", localAudioUrl);
                it.putExtra("title", localAudioTitle);
                LogUtil.e("local", "localAudioUrl = " + localAudioUrl + ", localAudioTitle = " + localAudioTitle);
                startActivity(it);
            }
        }, errorback());
        strinrequest.setTag("audiolesson");
        AppContext.getHttpQueue().add(strinrequest);
    }

    private Response.ErrorListener errorback() { // 抽出错误回调
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong_BACK", "联接错误原因： " + error);
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GongyiAudio audio = (GongyiAudio) audioList.get(position);
        String localid = audio.id;
        getDetailJson(URL_DETAIL + localid); // 执行Volley 拿Audio的 url
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_audiolesson_back:
                finish();
                break;
            case R.id.btn_audiolesson_listening:
                ObjectAnimator mAnimator1 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator1.setDuration(500).start();
                String URL_LISTENING = URL_LIST + "?type=1";
                audioList.clear();
                adapter.notifyDataSetChanged();
                getListJson(URL_LISTENING);
                break;
            case R.id.btn_audiolesson_reading:
                ObjectAnimator mAnimator2 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator2.setDuration(500).start();
                String URL_READING = URL_LIST + "?type=3";
                audioList.clear();
                adapter.notifyDataSetChanged();
                getListJson(URL_READING);
                break;
            case R.id.btn_audiolesson_speaking:
                ObjectAnimator mAnimator3 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator3.setDuration(500).start();
                String URL_SPEAKING = URL_LIST + "?type=7";
                audioList.clear();
                adapter.notifyDataSetChanged();
                getListJson(URL_SPEAKING);
                break;
            case R.id.btn_audiolesson_writting:
                ObjectAnimator mAnimator4 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator4.setDuration(500).start();
                String URL_WRITTING = URL_LIST + "?type=4";
                audioList.clear();
                adapter.notifyDataSetChanged();
                getListJson(URL_WRITTING);
                break;
        }
    }
}
