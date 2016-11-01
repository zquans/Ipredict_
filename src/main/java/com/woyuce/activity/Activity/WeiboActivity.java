//package com.woyuce.activity.Activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ViewFlipper;
//
//import com.woyuce.activity.R;
//
///**
// * Created by Administrator on 2016/9/23.
// */
//
//
//public class WeiboActivity extends Activity {
//
//    private ViewFlipper mFlipper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_weibo);
//
//        initView();
//    }
//
//    private void initView() {
//        mFlipper = (ViewFlipper) findViewById(R.id.vf_activity_weibo);
//
//        //ViewFlipper加入轮播图
//        for (int i = 0; i < 4; i++) {
//            ImageView mImg = new ImageView(this);
//            mImg.setBackgroundResource(R.mipmap.img_duck);
//            mFlipper.addView(mImg);
//        }
//        mFlipper.setInAnimation(this, R.anim.left_in);
//        mFlipper.setOutAnimation(this, R.anim.left_out);
//        mFlipper.startFlipping();
//    }
//
//
//    public void toPulish(View view) {
//        startActivity(new Intent(this,WeiboPulishActivity2.class));
//    }
//
//}
