package com.woyuce.activity.Act;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.woyuce.activity.Bean.SpeakingBean;
import com.woyuce.activity.R;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtBack, txtName, txtTime, txtRoom, txtContent;
    private SpeakingBean localspeaking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingdetail);

        initView();
        initEvent();
    }

    private void initView() {
        localspeaking = (SpeakingBean) getIntent().getSerializableExtra("localspeaking");
        
        txtBack = (TextView) findViewById(R.id.txt_speakingdetail_back);
        txtName = (TextView) findViewById(R.id.txt_speakingdetail_username);
        txtTime = (TextView) findViewById(R.id.txt_speakingdetail_examtime);
        txtRoom = (TextView) findViewById(R.id.txt_speakingdetail_examroom);
        txtContent = (TextView) findViewById(R.id.txt_speakingdetail_content);

        txtBack.setOnClickListener(this);
    }

    private void initEvent() {
        txtName.setText(localspeaking.uname);
        txtTime.setText(localspeaking.vtime);
        txtRoom.setText(localspeaking.examroom);
        txtContent.setText(localspeaking.message);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}