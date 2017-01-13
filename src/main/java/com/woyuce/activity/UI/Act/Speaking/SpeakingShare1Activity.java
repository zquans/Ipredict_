package com.woyuce.activity.UI.Act.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.UI.Act.BaseActivity;
import com.woyuce.activity.UI.Act.MainActivity;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingShare1Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LinearLayout llBack;
    private ImageView mImgBack;

    private Button btnNext, btnBack, btnRoomChoose;
    private TextView userName;
    private Spinner spnExamTime;

    private List<String> timeList = new ArrayList<>();
    private ArrayAdapter<String> timeAdapter;

    private String URL_TIME = "http://iphone.ipredicting.com/ksexamtime.aspx";
    private String localRoom, localRoomID, localTime;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("share");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingshare1);

        initView();
        initEvent();
        getTimeList();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llBack = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnNext = (Button) findViewById(R.id.button_share_next);
        btnBack = (Button) findViewById(R.id.button_share_back);
        btnRoomChoose = (Button) findViewById(R.id.btn_share_RoomChoose);
        userName = (TextView) findViewById(R.id.txt_share_userName);
        spnExamTime = (Spinner) findViewById(R.id.spn_share_examTime);

        mImgBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRoomChoose.setOnClickListener(this);
    }

    private void initEvent() { // Text 显示用户昵称
        String init = PreferenceUtil.getSharePre(this).getString("mUserName", "").toString();
        if (init != "") {
            userName.setText(init);
        } else {
            userName.setText("您还没有登陆哦");
        }
    }

    private void getTimeList() {
        StringRequest strinRequest = new StringRequest(Request.Method.GET, URL_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String examtime;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            examtime = jsonObject.getString("examtime");
                            timeList.add(examtime); // 读取考场事件
                        }
                    } else {
                        LogUtil.e("code!=0 DATA_BACK", "读取页面失败： " + jsonObject.getString("message"));
                    }
                    setTimeData(); // 数据加载完成后再放入
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-BACK", "连接错误原因： " + error.getMessage()); // 做错误处理
            }
        });
        strinRequest.setTag("share");
        AppContext.getHttpQueue().add(strinRequest);
    }

    private void setTimeData() {
        timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeList);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExamTime.setAdapter(timeAdapter);
        spnExamTime.setOnItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                localRoom = data.getExtras().getString("localRoom");
                localRoomID = data.getExtras().getString("localRoomID");
                btnRoomChoose.setText(localRoom);
                LogUtil.e("ALL", "all room = " + localRoom + ", " + localRoomID); // Log数据返回
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        localTime = timeList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_speaking_stastis:
                // 但界面看起来无跳转变化
                Intent it_statis = new Intent(this, SpeakingStatisActivity.class);
                startActivity(it_statis);
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.button_share_back:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.btn_share_RoomChoose:
                Intent it_roomid = new Intent(this, SpeakingChooseRoomActivity.class);
                startActivityForResult(it_roomid, 1);
                break;
            case R.id.button_share_next: // *** 点击"下一步"按钮,启动下一个"分享"界面
                if (localRoom == null || localRoom == "") {
                    ToastUtil.showMessage(SpeakingShare1Activity.this, "请选择考场");
                } else {
                    Intent it_share2 = new Intent(this, SpeakingShare2Activity.class);
                    it_share2.putExtra("localRoom", localRoom);
                    it_share2.putExtra("localRoomID", localRoomID);
                    it_share2.putExtra("localTime", localTime);
                    startActivity(it_share2);
                    overridePendingTransition(0, 0);
                }
                break;
        }
    }
}