package com.woyuce.activity.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.Fragment.FragmentStore1;
import com.woyuce.activity.Fragment.Fragmentfive;
import com.woyuce.activity.Fragment.Fragmentone;
import com.woyuce.activity.R;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    private LinearLayout mLinearlayout1, mLinearlayout2, mLinearlayout3;
    private ImageView mImgtab1, mImgtab2, mImgtab3;
    private TextView mTxt1, mTxt2, mTxt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store);

        initView();
        initEvent();
        setSelect(0);
    }

    private void initView() {
        mLinearlayout1 = (LinearLayout) findViewById(R.id.linearlayout_store_one);
        mLinearlayout2 = (LinearLayout) findViewById(R.id.linearlayout_store_two);
        mLinearlayout3 = (LinearLayout) findViewById(R.id.linearlayout_store_three);

        mImgtab1 = (ImageView) findViewById(R.id.icon_home);
        mImgtab2 = (ImageView) findViewById(R.id.icon_client);
        mImgtab3 = (ImageView) findViewById(R.id.icon_mine);

        mTxt1 = (TextView) findViewById(R.id.txt_store_home);
        mTxt2 = (TextView) findViewById(R.id.txt_store_shopcar);
        mTxt3 = (TextView) findViewById(R.id.txt_store_mine);

        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_store);
        mViewPager.setOnPageChangeListener(this);
        mFragments = new ArrayList<>();
        Fragment mStore01 = new FragmentStore1();
        Fragment mTab01 = new Fragmentone();
        Fragment mTab03 = new Fragmentfive();

        mFragments.add(mStore01);
        mFragments.add(mTab01);
        mFragments.add(mTab03);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2); // 设置保留4个界面的缓存
    }

    private void initEvent() {
        mLinearlayout1.setOnClickListener(this);
        mLinearlayout2.setOnClickListener(this);
        mLinearlayout3.setOnClickListener(this);
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    private void setTab(int i) {
        resetImg();
        switch (i) {
            case 0:
                mImgtab1.setImageResource(R.mipmap.icon_home_pressed);
                mTxt1.setTextColor(Color.parseColor("#f25f11"));
                break;
            case 1:
                mImgtab2.setImageResource(R.mipmap.icon_client_pressed);
                mTxt2.setTextColor(Color.parseColor("#f25f11"));
                break;
            case 2:
                mImgtab3.setImageResource(R.mipmap.icon_mine_pressed);
                mTxt3.setTextColor(Color.parseColor("#f25f11"));
                break;
        }
    }

    // 每次点击后,图片及文字回复原状
    private void resetImg() {
        mImgtab1.setImageResource(R.mipmap.icon_home);
        mTxt1.setTextColor(Color.parseColor("#707070"));
        mImgtab2.setImageResource(R.mipmap.icon_client);
        mTxt2.setTextColor(Color.parseColor("#707070"));
        mImgtab3.setImageResource(R.mipmap.icon_mine);
        mTxt3.setTextColor(Color.parseColor("#707070"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_store_one:
                setSelect(0);
                break;
            case R.id.linearlayout_store_two:
                setSelect(1);
                break;
            case R.id.linearlayout_store_three:
                setSelect(2);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        setSelect(arg0);
    }
}