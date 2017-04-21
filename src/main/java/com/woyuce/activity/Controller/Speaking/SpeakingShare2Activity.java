package com.woyuce.activity.Controller.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingShare2Activity extends BaseActivity implements View.OnClickListener {

    private ImageView mImgBack;
    private Button btnBack, btnNext;
    private LinearLayout llBack;
    private TextView txtExamRoom, txtExamTime;
    private EditText edtMessage;

    private String localRoom, localTime, localRoomID; // 上一级传来的数据,其中id和time要传到下一级

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_share2);

        initView();
        initEvent();
    }

    private void initView() {
        localRoom = getIntent().getStringExtra("localRoom");
        localRoomID = getIntent().getStringExtra("localRoomID");
        localTime = getIntent().getStringExtra("localTime");

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
            case R.id.ll_speaking_stastis: // **** 点击" 高频统计" 启动 Activity-统计，
                // 但界面看起来无跳转变化
                startActivity(new Intent(this, SpeakingStatisActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.button_share2_back:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.button_share2_next: // **** 点击"下一步"按钮， 进入下一个"分享"界面
                String localMessage = edtMessage.getText().toString();
                Intent intent = new Intent(this, SpeakingShare3Activity.class);
                intent.putExtra("localMessage", localMessage);
                intent.putExtra("localTime", localTime);
                intent.putExtra("localRoomID", localRoomID);
                intent.putExtra("localRoom", localRoom);
                LogUtil.e("aLL info = " + localRoomID + " , " + localMessage + " ," + localTime);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }
}