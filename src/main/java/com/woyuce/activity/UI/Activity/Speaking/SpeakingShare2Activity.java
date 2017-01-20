package com.woyuce.activity.UI.Activity.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Activity.MainActivity;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingShare2Activity extends BaseActivity implements View.OnClickListener {

    private ImageView mImgBack;
    private Button btnBack, btnNext;
    private LinearLayout llBack;
    private TextView txtExamRoom, txtExamTime;
    private EditText edtMessage;

    private String localRoom, localTime, localRoomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingshare2);

        initView();
        initEvent();
    }

    private void initView() {
        Intent it_share2 = getIntent();
        localRoom = it_share2.getStringExtra("localRoom");
        localRoomID = it_share2.getStringExtra("localRoomID");
        localTime = it_share2.getStringExtra("localTime");

        mImgBack = (ImageView) findViewById(R.id.img_back);
        txtExamRoom = (TextView) findViewById(R.id.txt_share2_examRoom);
        txtExamTime = (TextView) findViewById(R.id.txt_share2_examTime);
        edtMessage = (EditText) findViewById(R.id.edit_share2_message);
        llBack = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnBack = (Button) findViewById(R.id.button_share2_back);
        btnNext = (Button) findViewById(R.id.button_share2_next);

        mImgBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initEvent() {
        txtExamRoom.setText(localRoom); // 显示考场名
        txtExamTime.setText(localTime); // 显示考试时间
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_speaking_stastis:
                Intent it_statis = new Intent(this, SpeakingStatisActivity.class);
                startActivity(it_statis);
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.button_share2_back:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.button_share2_next:
                String localMessage = edtMessage.getText().toString();
                Intent it_share3 = new Intent(this, SpeakingShare3Activity.class);
                it_share3.putExtra("localMessage", localMessage);
                it_share3.putExtra("localTime", localTime);
                it_share3.putExtra("localRoomID", localRoomID);
                it_share3.putExtra("localRoom", localRoom);
                startActivity(it_share3);
                overridePendingTransition(0, 0);
                break;
        }
    }
}