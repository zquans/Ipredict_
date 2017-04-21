package com.woyuce.activity.Controller.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingShare1Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LinearLayout llBack;
    private ImageView mImgBack;

    private Button btnNext, btnBack, btnRoomChoose;
    private TextView userName;
    private Spinner spnExamTime;

    private List<String> timeList = new ArrayList<>();
    private ArrayAdapter<String> timeAdapter;

    private String localRoom, localRoomID, localTime;

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_SPEAKING_SHARE_ONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_share1);

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
        if (!TextUtils.equals(init, "")) {
            userName.setText(init);
        } else {
            userName.setText("您还没有登陆哦");
        }
    }

    private void getTimeList() {
        HttpUtil.get(Constants.URL_GET_SPEAKING_SHARE_ONE, Constants.ACTIVITY_SPEAKING_SHARE_ONE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            timeList.add(jsonObject.getString("examtime")); // 考试时间
                        }
                    }
                    setTimeData(); // 数据加载完成后再放入
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                startActivity(new Intent(this, SpeakingStatisActivity.class));
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
                startActivityForResult(new Intent(this, SpeakingChooseRoomActivity.class), Constants.CODE_START_ACTIVITY_FOR_RESULT);
                break;
            case R.id.button_share_next:
                // 点击"下一步"按钮,启动下一个"分享"界面
                if (TextUtils.isEmpty(localRoom)) {
                    ToastUtil.showMessage(SpeakingShare1Activity.this, "请选择考场");
                } else {
                    Intent intent = new Intent(this, SpeakingShare2Activity.class);
                    intent.putExtra("localRoom", localRoom);
                    intent.putExtra("localRoomID", localRoomID);
                    intent.putExtra("localTime", localTime);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                break;
        }
    }
}