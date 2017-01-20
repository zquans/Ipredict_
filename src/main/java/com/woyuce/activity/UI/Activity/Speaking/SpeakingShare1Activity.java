package com.woyuce.activity.UI.Activity.Speaking;

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

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Activity.MainActivity;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingShare1Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LinearLayout llBack;
    private ImageView mImgBack;

    private Button btnNext, btnBack, btnRoomChoose;
    private TextView userName;
    private Spinner spnExamTime;

    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayAdapter<String> timeAdapter;

    private String localRoom, localRoomID, localTime;

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SPEAKING_SHARE_ONE);
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

    private void initEvent() {
        // Text 显示用户昵称
        String init = PreferenceUtil.getSharePre(this).getString("mUserName", "").toString();
        if (init != "") {
            userName.setText(init);
        } else {
            userName.setText("您还没有登陆哦");
        }
    }

    private void getTimeList() {
        OkGo.get(Constants.URL_GET_SPEAKING_SHARE_ONE).tag(Constants.ACTIVITY_SPEAKING_SHARE_ONE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
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
            }
            setTimeData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            case Constants.CODE_START_ACTIVITY_FOR_RESULT:
                localRoom = data.getExtras().getString("localRoom");
                localRoomID = data.getExtras().getString("localRoomID");
                btnRoomChoose.setText(localRoom);
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
                startActivityForResult(it_roomid, Constants.CODE_START_ACTIVITY_FOR_RESULT);
                break;
            case R.id.button_share_next:
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