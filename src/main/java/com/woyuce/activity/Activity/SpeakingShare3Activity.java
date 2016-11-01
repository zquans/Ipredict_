package com.woyuce.activity.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.woyuce.activity.Fragment.FragmentPartOne;
import com.woyuce.activity.Fragment.FragmentPartTwo;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingShare3Activity extends BaseActivity implements View.OnClickListener {

    private ImageView mImgBack;
    private Button btnBack, btnNext;
    private LinearLayout llBack ,btnPartOne, btnPartTwo;

    // 上一级传来的数据,还需要传递到下一级
    private String localRoomID, localTime, localMessage ,localRoom;
    //Fragment中传递到下一级数据
    private String localsubname;
    private List<String> subidList = new ArrayList<>();
    private List<String> subnameList = new ArrayList<>();

    private FragmentPartOne fragmentpartone;
    private FragmentPartTwo fragmentparttwo;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingshare3);

        initView();
        initEvent();
    }

    private void initView() {
        Intent intent = getIntent();
        localMessage = intent.getStringExtra("localMessage");
        localTime = intent.getStringExtra("localTime");
        localRoomID = intent.getStringExtra("localRoomID");
        localRoom = intent.getStringExtra("localRoom");

        mImgBack = (ImageView) findViewById(R.id.img_back);
        llBack = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnBack = (Button) findViewById(R.id.button_share3_back);
        btnNext = (Button) findViewById(R.id.button_share3_next);
        btnPartOne = (LinearLayout) findViewById(R.id.btn_share3_part1);
        btnPartTwo = (LinearLayout) findViewById(R.id.btn_share3_part2);

        mImgBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPartOne.setOnClickListener(this);
        btnPartTwo.setOnClickListener(this);

        fragmentManager = getFragmentManager();
    }

    private void initEvent() {
        fragmentpartone = new FragmentPartOne();
        FragmentTransaction transactionPart = fragmentManager.beginTransaction();
        transactionPart.add(R.id.framelayout_share3, fragmentpartone).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_speaking_stastis: // **** 点击" 高频统计" 启动 Activity-统计，
                Intent it_statis = new Intent(this, SpeakingStatisActivity.class);
                startActivity(it_statis);
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.button_share3_back:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.btn_share3_part1:
                btnPartOne.setBackgroundResource(R.drawable.buttonstyle);
                btnPartTwo.setBackgroundResource(R.drawable.buttonstyle_lightgray);
                FragmentTransaction transactionPart1 = fragmentManager.beginTransaction();
                if (fragmentpartone == null) {
                    fragmentpartone = new FragmentPartOne();
                    transactionPart1.add(R.id.framelayout_share3, fragmentpartone).commit();
                    break;
                }
                if (fragmentparttwo != null) {
                    transactionPart1.hide(fragmentparttwo);
                }
                transactionPart1.show(fragmentpartone);
                transactionPart1.commit();
                break;
            case R.id.btn_share3_part2:
                btnPartTwo.setBackgroundResource(R.drawable.buttonstyle);
                btnPartOne.setBackgroundResource(R.drawable.buttonstyle_lightgray);
                FragmentTransaction transactionPart2 = fragmentManager.beginTransaction();
                if (fragmentparttwo == null) {
                    fragmentparttwo = new FragmentPartTwo();
                    transactionPart2.add(R.id.framelayout_share3, fragmentparttwo).commit();
                    break;
                }
                if (fragmentpartone != null) {
                    transactionPart2.hide(fragmentpartone);
                }
                transactionPart2.show(fragmentparttwo);
                transactionPart2.commit();
                break;
            case R.id.button_share3_next: // *** 点击"下一步"按钮,启动下一个"分享"界面
                if (fragmentparttwo == null) {
                    fragmentparttwo = new FragmentPartTwo(); // 先判断有无part2,如果没有，则初始化一个，如果有，则直接比较
                    FragmentTransaction transactionPart = fragmentManager.beginTransaction();
                    transactionPart.add(R.id.framelayout_share3, fragmentparttwo).commit();
                }
                subidList = fragmentpartone.returnSubid1(); // initView中已经初始化了part1，无需再初始化,直接调用方法
                String subid2 = fragmentparttwo.returnSubid2(); // 实例化part2,否则会报空指针异常

                //从Fragment中传给下一级的name
                subnameList = fragmentpartone.returnSubnameList();
                localsubname = fragmentparttwo.returnSubname2();

                if (subidList.size() == 0 && subid2 == null) {
                    ToastUtil.showMessage(SpeakingShare3Activity.this, "请选择要分享的题目,亲");
                    return;                         //这个return太妙
                } else if (subidList.size() == 0 && subid2 != null) {
                    subidList.add(subid2);
                } else if (subidList.size() !=0 && subid2 == null) {
                }
                else if (subidList.size() !=0 && subid2 != null) {
                    subidList.add(subid2);
                }
                Intent it_share4 = new Intent(this, SpeakingShare4Activity.class);
                it_share4.putExtra("localMessage", localMessage);
                it_share4.putExtra("localTime", localTime);
                it_share4.putExtra("localRoomID", localRoomID);
                it_share4.putStringArrayListExtra("subidList", (ArrayList<String>) subidList);
                it_share4.putExtra("localRoom", localRoom);
                //传入Fragmentpart2中的subname
                it_share4.putExtra("localsubname", localsubname);
                //传入Fragmentpart1中的subnameList
                it_share4.putExtra("localsubname", localsubname);
                it_share4.putStringArrayListExtra("subnameList", (ArrayList<String>) subnameList);
                startActivity(it_share4);
                overridePendingTransition(0, 0);
                break;
        }
    }
}