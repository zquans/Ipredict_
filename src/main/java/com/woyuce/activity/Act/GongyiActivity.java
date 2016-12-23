package com.woyuce.activity.Act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.R;

public class GongyiActivity extends Activity implements OnClickListener {

    private TextView titleback;
    private LinearLayout llAudio, llTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gongyi);

        initView();
        initEvent();
    }

    private void initView() {
        titleback = (TextView) findViewById(R.id.txt_gongyi_title);
        llAudio = (LinearLayout) findViewById(R.id.ll_gongyi_audio);
        llTencent = (LinearLayout) findViewById(R.id.ll_gongyi_tencent);

        titleback.setOnClickListener(this);
        llAudio.setOnClickListener(this);
        llTencent.setOnClickListener(this);
    }

    private void initEvent() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_gongyi_title:
                finish();
                break;
            case R.id.ll_gongyi_audio:
                startActivity(new Intent(this, GongyiLessonActivity.class));
                break;
            case R.id.ll_gongyi_tencent:
                Intent intent = new Intent(this, WebNoCookieActivity.class);
                intent.putExtra("URL", "https://iyuce.ke.qq.com/");
                intent.putExtra("TITLE", "我预测腾讯课堂");
                intent.putExtra("COLOR", "#366090");
                startActivity(intent);
                break;
        }
    }
}