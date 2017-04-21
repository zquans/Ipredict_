package com.woyuce.activity.Controller.Speaking;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Model.Speaking.SpeakingBean;
import com.woyuce.activity.R;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtBack, txtName, txtTime, txtRoom, txtContent;
    private SpeakingBean mSpeakingBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_detail);

        initView();
        initEvent();
    }

    private void initView() {
        mSpeakingBean = (SpeakingBean) getIntent().getSerializableExtra("SpeakingBean");

        txtBack = (TextView) findViewById(R.id.txt_speakingdetail_back);
        txtName = (TextView) findViewById(R.id.txt_speakingdetail_username);
        txtTime = (TextView) findViewById(R.id.txt_speakingdetail_examtime);
        txtRoom = (TextView) findViewById(R.id.txt_speakingdetail_examroom);
        txtContent = (TextView) findViewById(R.id.txt_speakingdetail_content);

        txtBack.setOnClickListener(this);
    }

    private void initEvent() {
        txtName.setText(mSpeakingBean.uname);
        txtTime.setText(mSpeakingBean.vtime);
        txtRoom.setText(mSpeakingBean.examroom);
        txtContent.setText(mSpeakingBean.message);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}